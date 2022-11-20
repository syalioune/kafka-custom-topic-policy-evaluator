package fr.syalioune.kafka.policy;

import org.apache.kafka.common.config.TopicConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TopicPolicyTest {

    @Test
    public void testDefaultValue() {
        // Arrange
        // Nothing to be done

        // Act
        TopicPolicy topicPolicy = new TopicPolicy();

        // Assert
        Assertions.assertEquals(Integer.MAX_VALUE, topicPolicy.getOrder(), () -> "The default order is not correct");
        Assertions.assertEquals(0, topicPolicy.getMinPartitions(), () -> "The default minimum number of partitions is not correct");
        Assertions.assertEquals(Integer.MAX_VALUE, topicPolicy.getMaxPartitions(), () -> "The default maximum number of partitions is not correct");
        Assertions.assertEquals(0, topicPolicy.getMinReplicationFactor(), () -> "The default minimum replication factor is not correct");
        Assertions.assertEquals(Integer.MAX_VALUE, topicPolicy.getMaxReplicationFactor(), () -> "The default maximum replication factor is not correct");
        Assertions.assertEquals(0, topicPolicy.getMinIsr(), () -> "The default minimum ISR is not correct");
        Assertions.assertEquals(Integer.MAX_VALUE, topicPolicy.getMaxIsr(), () -> "The default maximum ISR is not correct");
        Assertions.assertEquals(List.of(TopicConfig.CLEANUP_POLICY_COMPACT, TopicConfig.CLEANUP_POLICY_DELETE), topicPolicy.getAllowedCleanupPolicies(), () -> "The default allowable list of cleanup policy is not correct");
        Assertions.assertEquals(Duration.ofHours(1).toMillis(), topicPolicy.getMinRetentionMs(), () -> "The default minimum retention period is not correct");
        Assertions.assertEquals(Long.MAX_VALUE, topicPolicy.getMaxRetentionMs(), () -> "The default maximum retention period is not correct");
    }
}
