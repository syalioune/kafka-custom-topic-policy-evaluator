package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CleanupPolicyEvaluatorTest {

    private PolicyEvaluator policyEvaluator;

    @BeforeEach
    public void setup() {
        policyEvaluator = new CleanupPolicyEvaluator();
    }

    @ParameterizedTest
    @MethodSource("validCleanupPolicyScenarioProvider")
    public void testValidCleanupPolicyScenario(List<String> allowablePolicies, Map<String, String> topicConfiguration) {
        // Arrange
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setAllowedCleanupPolicies(allowablePolicies);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                -1,
                (short) -1,
                Collections.emptyMap(),
                topicConfiguration
        );

        // Act
        policyEvaluator.evaluate(requestMetadata, policy);

        // Assert
        // No exception thrown
    }

    @Test
    public void testInvalidCleanupPolicyScenario() {
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setAllowedCleanupPolicies(List.of(TopicConfig.CLEANUP_POLICY_DELETE));
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                -1,
                (short) -1,
                Collections.emptyMap(),
                Map.of(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(
                PolicyViolationException.class,
                () -> policyEvaluator.evaluate(requestMetadata, policy)
        );

        // Assert
        Assertions.assertEquals(
                "The configured cleanup policy ("+TopicConfig.CLEANUP_POLICY_COMPACT+") for topic test-topic is not allowed by policy "+policy.getName(),
                exception.getMessage()
        );
    }

    private static Stream<Arguments> validCleanupPolicyScenarioProvider() {
        return Stream.of(
                Arguments.of(List.of(TopicConfig.CLEANUP_POLICY_DELETE, TopicConfig.CLEANUP_POLICY_COMPACT), Collections.emptyMap()),
                Arguments.of(List.of(TopicConfig.CLEANUP_POLICY_DELETE), Map.of(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE))
        );
    }
}
