package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class RetentionEvaluatorTest {

    private PolicyEvaluator policyEvaluator;

    @BeforeEach
    public void setup() {
        policyEvaluator = new RetentionEvaluator();
    }

    @ParameterizedTest
    @MethodSource("validRetentionScenarioProvider")
    public void testValidRetentionScenario(int minimum, int maximum, Map<String, String> topicConfiguration) {
        // Arrange
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinRetentionMs(minimum);
        policy.setMaxRetentionMs(maximum);
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

    @ParameterizedTest
    @MethodSource("invalidRetentionScenarioProvider")
    public void testInvalidRetentionScenario(int minimum, int maximum, Map<String, String> topicConfiguration) {
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinRetentionMs(minimum);
        policy.setMaxRetentionMs(maximum);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                -1,
                (short) -1,
                Collections.emptyMap(),
               topicConfiguration
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(
                PolicyViolationException.class,
                () -> policyEvaluator.evaluate(requestMetadata, policy)
        );

        // Assert
        Assertions.assertEquals(
                "The configured retention ("+topicConfiguration.get(TopicConfig.RETENTION_MS_CONFIG)+"ms) for topic test-topic does not fit in the allowed range ["+policy.getMinRetentionMs()+";"+policy.getMaxRetentionMs()+"] of policy "+policy.getName(),
                exception.getMessage()
        );
    }

    private static Stream<Arguments> validRetentionScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3, Collections.emptyMap()),
                Arguments.of(1,3, Map.of(TopicConfig.RETENTION_MS_CONFIG, "2")),
                Arguments.of(1,3, Map.of(TopicConfig.RETENTION_MS_CONFIG, "1")),
                Arguments.of(1,3, Map.of(TopicConfig.RETENTION_MS_CONFIG, "3"))
        );
    }

    private static Stream<Arguments> invalidRetentionScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3, Map.of(TopicConfig.RETENTION_MS_CONFIG, "0")),
                Arguments.of(1,3, Map.of(TopicConfig.RETENTION_MS_CONFIG, "4"))
        );
    }
}
