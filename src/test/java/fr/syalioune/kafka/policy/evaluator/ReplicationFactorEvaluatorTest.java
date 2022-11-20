package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

public class ReplicationFactorEvaluatorTest {

    private PolicyEvaluator policyEvaluator;

    @BeforeEach
    public void setup() {
        policyEvaluator = new ReplicationFactorEvaluator();
    }

    @ParameterizedTest
    @MethodSource("validReplicationFactorScenarioProvider")
    public void testValidReplicationFactorScenario(int minimum, int maximum, short replicationFactor) {
        // Arrange
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinReplicationFactor(minimum);
        policy.setMaxReplicationFactor(maximum);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                -1,
                replicationFactor,
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        // Act
        policyEvaluator.evaluate(requestMetadata, policy);

        // Assert
        // No exception thrown
    }

    @ParameterizedTest
    @MethodSource("invalidReplicationFactorScenarioProvider")
    public void testInvalidReplicationFactorScenario(int minimum, int maximum, short replicationFactor) {
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinReplicationFactor(minimum);
        policy.setMaxReplicationFactor(maximum);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                -1,
                replicationFactor,
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        // Act
        PolicyViolationException exception = Assertions.assertThrows(
                PolicyViolationException.class,
                () -> policyEvaluator.evaluate(requestMetadata, policy)
        );

        // Assert
        Assertions.assertEquals(
                "The configured replication factor ("+requestMetadata.replicationFactor()+") for topic test-topic does not fit in the allowed range ["+policy.getMinReplicationFactor()+";"+policy.getMaxReplicationFactor()+"] of policy "+policy.getName(),
                exception.getMessage()
        );
    }

    private static Stream<Arguments> validReplicationFactorScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3,(short) -1),
                Arguments.of(1,3,(short) 2),
                Arguments.of(1,3,(short) 1),
                Arguments.of(1,3,(short) 3)
        );
    }

    private static Stream<Arguments> invalidReplicationFactorScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3,(short) 0),
                Arguments.of(1,3,(short) 4)
        );
    }
}
