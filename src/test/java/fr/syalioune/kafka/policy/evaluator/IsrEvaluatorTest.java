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

public class IsrEvaluatorTest {

    private PolicyEvaluator policyEvaluator;

    @BeforeEach
    public void setup() {
        policyEvaluator = new IsrEvaluator();
    }

    @ParameterizedTest
    @MethodSource("validIsrScenarioProvider")
    public void testValidIsrScenario(int minimum, int maximum, Map<String, String> topicConfiguration) {
        // Arrange
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinIsr(minimum);
        policy.setMaxIsr(maximum);
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
    @MethodSource("invalidIsrScenarioProvider")
    public void testInvalidIsrScenario(int minimum, int maximum, Map<String, String> topicConfiguration) {
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinIsr(minimum);
        policy.setMaxIsr(maximum);
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
                "The configured min.insync.replicas ("+topicConfiguration.get(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG)+") for topic test-topic does not fit in the allowed range ["+policy.getMinIsr()+";"+policy.getMaxIsr()+"] of policy "+policy.getName(),
                exception.getMessage()
        );
    }

    private static Stream<Arguments> validIsrScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3, Collections.emptyMap()),
                Arguments.of(1,3, Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2")),
                Arguments.of(1,3, Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")),
                Arguments.of(1,3, Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "3"))
        );
    }

    private static Stream<Arguments> invalidIsrScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3, Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "0")),
                Arguments.of(1,3, Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "4"))
        );
    }
}
