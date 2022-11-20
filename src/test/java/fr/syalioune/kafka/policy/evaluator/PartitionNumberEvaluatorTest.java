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

public class PartitionNumberEvaluatorTest {

    private PolicyEvaluator policyEvaluator;

    @BeforeEach
    public void setup() {
        policyEvaluator = new PartitionNumberEvaluator();
    }

    @ParameterizedTest
    @MethodSource("validPartitionNumberScenarioProvider")
    public void testValidPartitionNumberScenario(int minimum, int maximum, int partitionNumber) {
        // Arrange
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinPartitions(minimum);
        policy.setMaxPartitions(maximum);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                partitionNumber,
                (short) -1,
                Collections.emptyMap(),
                Collections.emptyMap()
        );

        // Act
        policyEvaluator.evaluate(requestMetadata, policy);

        // Assert
        // No exception thrown
    }

    @ParameterizedTest
    @MethodSource("invalidPartitionNumberScenarioProvider")
    public void testInvalidPartitionNumberScenario(int minimum, int maximum, int partitionNumber) {
        TopicPolicy policy = new TopicPolicy();
        policy.setName("test-policy");
        policy.setMinPartitions(minimum);
        policy.setMaxPartitions(maximum);
        CreateTopicPolicy.RequestMetadata requestMetadata = new CreateTopicPolicy.RequestMetadata(
                "test-topic",
                partitionNumber,
                (short) -1,
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
                "The configured number of partitions ("+requestMetadata.numPartitions()+") for topic test-topic does not fit in the allowed range ["+policy.getMinPartitions()+";"+policy.getMaxPartitions()+"] of policy "+policy.getName(),
                exception.getMessage()
        );
    }

    private static Stream<Arguments> validPartitionNumberScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3,-1),
                Arguments.of(1,3,2),
                Arguments.of(1,3,1),
                Arguments.of(1,3,3)
        );
    }

    private static Stream<Arguments> invalidPartitionNumberScenarioProvider() {
        return Stream.of(
                Arguments.of(1,3,0),
                Arguments.of(1,3,4)
        );
    }
}
