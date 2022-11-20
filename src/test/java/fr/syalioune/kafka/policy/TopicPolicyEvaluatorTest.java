package fr.syalioune.kafka.policy;

import fr.syalioune.kafka.policy.evaluator.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.AlterConfigPolicy;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TopicPolicyEvaluatorTest {

    private TopicPolicyEvaluator policyEvaluator;

    @BeforeEach
    public void setup() {
        policyEvaluator = new TopicPolicyEvaluator();
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        // Nothing to be done

        // Act
        // Nothing to be done

        // Assert
        Assertions.assertEquals(false, policyEvaluator.isEnabled(), () -> "The topic policy evaluator is not disabled by default");
        Assertions.assertEquals(".*", policyEvaluator.getTopicNameGlobalPattern().pattern(), () -> "The default topic name global pattern is not correct");
        Assertions.assertEquals(Collections.emptyList(), policyEvaluator.getTopicExcludePatterns(), () -> "The default exclude patterns list is not correct");
        Assertions.assertEquals(5, policyEvaluator.getPolicyEvaluators().size(), () -> "The policy evaluator does not contains the correct number of evaluators");
        Assertions.assertTrue(policyEvaluator.getPolicyEvaluators().stream().anyMatch(evaluator -> evaluator instanceof CleanupPolicyEvaluator), () -> "The topic policy evaluator does not contain a cleanup evaluator");
        Assertions.assertTrue(policyEvaluator.getPolicyEvaluators().stream().anyMatch(evaluator -> evaluator instanceof IsrEvaluator), () -> "The topic policy evaluator does not contain an isr evaluator");
        Assertions.assertTrue(policyEvaluator.getPolicyEvaluators().stream().anyMatch(evaluator -> evaluator instanceof PartitionNumberEvaluator), () -> "The topic policy evaluator does not contain a partition number evaluator");
        Assertions.assertTrue(policyEvaluator.getPolicyEvaluators().stream().anyMatch(evaluator -> evaluator instanceof ReplicationFactorEvaluator), () -> "The topic policy evaluator does not contain a replication factor evaluator");
        Assertions.assertTrue(policyEvaluator.getPolicyEvaluators().stream().anyMatch(evaluator -> evaluator instanceof RetentionEvaluator), () -> "The topic policy evaluator does not contain a retention evaluator");
    }

    @Test
    public void testConfigurationParsing() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();

        // Act
        policyEvaluator.configure(brokerConfig);
        
        // Assert
        Assertions.assertEquals(true, policyEvaluator.isEnabled(), () -> "The topic policy evaluator should be enabled");
        Assertions.assertEquals(brokerConfig.get(TopicPolicyConfig.TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN), policyEvaluator.getTopicNameGlobalPattern().pattern(), () -> "The topic name global pattern is not correct");
        Assertions.assertEquals(2, policyEvaluator.getTopicExcludePatterns().size(), () -> "The exclude patterns list size is not correct");
        Assertions.assertEquals(2, policyEvaluator.getPolicies().size(), () -> "The policy list size is not correct");
        Assertions.assertEquals("abc", policyEvaluator.getPolicies().get(0).getName());
        Assertions.assertEquals(TopicConfig.CLEANUP_POLICY_DELETE, policyEvaluator.getPolicies().get(0).getAllowedCleanupPolicies().get(0));
    }

    @Test
    public void testDisabledTopicPolicyEvaluator() {
        // Arrange
        Map<String, String> brokerConfig = new HashMap<>();
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_ENABLE, "false");
        policyEvaluator.configure(brokerConfig);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                -1,
                (short) -1,
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        // Act
        policyEvaluator.validate(requestMetadata);

        // Assert
        // No exception thrown
    }

    @ParameterizedTest
    @ValueSource(strings = {"__consumer_offsets", "test-1"})
    public void testTopicPolicyEvaluatorWithExcludedTopic(String topic) {
        // Arrange
        Map<String, String> brokerConfig = new HashMap<>();
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_ENABLE, "true");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_EXCLUDE_PATTERNS, "__.*,^test.+");
        policyEvaluator.configure(brokerConfig);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                topic,
                -1,
                (short) -1,
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        // Act
        policyEvaluator.validate(requestMetadata);

        // Assert
        // No exception thrown
    }

    @Test
    public void testValidCreateTopicRequest() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();
        policyEvaluator.configure(brokerConfig);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "abc-abc-test-1",
                2,
                (short) 3,
                Collections.emptyMap(),
                Map.of(
                        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        "2",
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_DELETE,
                        TopicConfig.RETENTION_MS_CONFIG,
                        "3"
                )
        );

        // Act
        policyEvaluator.validate(requestMetadata);

        // Assert
        // No exceptions thrown
    }

    @Test
    public void testValidAlterTopicRequest() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();
        policyEvaluator.configure(brokerConfig);
        AlterConfigPolicy.RequestMetadata requestMetadata = new AlterConfigPolicy.RequestMetadata(
                new ConfigResource(ConfigResource.Type.TOPIC, "abc-abc-test-1"),
                Map.of(
                        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        "2",
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_DELETE,
                        TopicConfig.RETENTION_MS_CONFIG,
                        "3"
                )
        );

        // Act
        policyEvaluator.validate(requestMetadata);

        // Assert
        // No exceptions thrown
    }

    @Test
    public void testInvalidAlterTopicRequestWithBadIsr() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();
        policyEvaluator.configure(brokerConfig);
        AlterConfigPolicy.RequestMetadata requestMetadata = new AlterConfigPolicy.RequestMetadata(
                new ConfigResource(ConfigResource.Type.TOPIC, "abc-default-test-1"),
                Map.of(
                        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        "10",
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_DELETE,
                        TopicConfig.RETENTION_MS_CONFIG,
                        "3"
                )
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(PolicyViolationException.class, () -> policyEvaluator.validate(requestMetadata));

        // Assert
        Assertions.assertEquals(
                "The configured min.insync.replicas ("+requestMetadata.configs().get(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG)+") for topic "+requestMetadata.resource().name()+" does not fit in the allowed range [1;5] of policy default",
                exception.getMessage()
        );
    }

    @Test
    public void testInvalidCreateTopicRequestNotMatchingGlobalNamePattern() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();
        policyEvaluator.configure(brokerConfig);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "policy-topic",
                2,
                (short) 3,
                Collections.emptyMap(),
                Map.of(
                        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        "2",
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_DELETE,
                        TopicConfig.RETENTION_MS_CONFIG,
                        "3"
                )
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(PolicyViolationException.class, () -> policyEvaluator.validate(requestMetadata));

        // Assert
        Assertions.assertEquals(
                "The topic "+requestMetadata.topic()+" does not match the global topic name pattern "+policyEvaluator.getTopicNameGlobalPattern().pattern(),
                exception.getMessage()
        );
    }

    @Test
    public void testInvalidCreateTopicRequestNotMatchingAPolicy() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();
        policyEvaluator.configure(brokerConfig);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "pqr-topic",
                2,
                (short) 3,
                Collections.emptyMap(),
                Map.of(
                        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        "2",
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_DELETE,
                        TopicConfig.RETENTION_MS_CONFIG,
                        "3"
                )
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(PolicyViolationException.class, () -> policyEvaluator.validate(requestMetadata));

        // Assert
        Assertions.assertEquals(
                "The topic "+requestMetadata.topic()+" does not match with any of the configured policy. Creation denied.",
                exception.getMessage()
        );
    }

    @Test
    public void testInvalidCreateTopicRequestNotMatchingPolicyRules() {
        // Arrange
        Map<String, String> brokerConfig = createBrokerConfiguration();
        policyEvaluator.configure(brokerConfig);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "abc-abc-topic",
                10,
                (short) 3,
                Collections.emptyMap(),
                Map.of(
                        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        "2",
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_DELETE,
                        TopicConfig.RETENTION_MS_CONFIG,
                        "3"
                )
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(PolicyViolationException.class, () -> policyEvaluator.validate(requestMetadata));

        // Assert
        Assertions.assertEquals(
                "The configured number of partitions ("+requestMetadata.numPartitions()+") for topic "+requestMetadata.topic()+" does not fit in the allowed range [1;5] of policy abc",
                exception.getMessage()
        );
    }

    private Map<String, String> createBrokerConfiguration() {
        Map<String, String> brokerConfig = new HashMap<>();
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_ENABLE, "true");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_EXCLUDE_PATTERNS, "__.*,^test.+");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN, "[a-z]{3}-.+");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICIES, "default,abc");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_PATTERN_SUFFIX, "[a-z]{3}-default-.+");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_ORDER_SUFFIX, "10");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_ISR_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_ISR_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".default." + TopicPolicyConfig.TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX, TopicConfig.CLEANUP_POLICY_COMPACT);
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_PATTERN_SUFFIX, "[a-z]{3}-abc-.+");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_ORDER_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_ISR_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_ISR_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX, "1");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX, "5");
        brokerConfig.put(TopicPolicyConfig.TOPIC_POLICY_PREFIX + ".abc." + TopicPolicyConfig.TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX, TopicConfig.CLEANUP_POLICY_DELETE);
        return brokerConfig;
    }

}
