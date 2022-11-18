package fr.syalioune.kafka.policy;

import org.apache.kafka.common.config.TopicConfig;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Topic policy POJO.
 */
public class MyTopicPolicy {

    private String name;

    private Pattern regexp;

    private int minPartitions = 0;

    private int maxPartitions = Integer.MAX_VALUE;

    private int minReplicationFactor = 0;

    private int maxReplicationFactor = Integer.MAX_VALUE;

    private int minIsr = 0;

    private int maxIsr = Integer.MAX_VALUE;

    private List<String> allowedCleanupPolicies = List.of(TopicConfig.CLEANUP_POLICY_COMPACT, TopicConfig.CLEANUP_POLICY_DELETE);

    private long minRetentionMs = 0;

    private long maxRetentionMs = Long.MAX_VALUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getRegexp() {
        return regexp;
    }

    public void setRegexp(Pattern regexp) {
        this.regexp = regexp;
    }

    public int getMinPartitions() {
        return minPartitions;
    }

    public void setMinPartitions(int minPartitions) {
        this.minPartitions = minPartitions;
    }

    public int getMaxPartitions() {
        return maxPartitions;
    }

    public void setMaxPartitions(int maxPartitions) {
        this.maxPartitions = maxPartitions;
    }

    public int getMinReplicationFactor() {
        return minReplicationFactor;
    }

    public void setMinReplicationFactor(int minReplicationFactor) {
        this.minReplicationFactor = minReplicationFactor;
    }

    public int getMaxReplicationFactor() {
        return maxReplicationFactor;
    }

    public void setMaxReplicationFactor(int maxReplicationFactor) {
        this.maxReplicationFactor = maxReplicationFactor;
    }

    public int getMinIsr() {
        return minIsr;
    }

    public void setMinIsr(int minIsr) {
        this.minIsr = minIsr;
    }

    public int getMaxIsr() {
        return maxIsr;
    }

    public void setMaxIsr(int maxIsr) {
        this.maxIsr = maxIsr;
    }

    public List<String> getAllowedCleanupPolicies() {
        return allowedCleanupPolicies;
    }

    public void setAllowedCleanupPolicies(List<String> allowedCleanupPolicies) {
        this.allowedCleanupPolicies = allowedCleanupPolicies;
    }

    public long getMinRetentionMs() {
        return minRetentionMs;
    }

    public void setMinRetentionMs(long minRetentionMs) {
        this.minRetentionMs = minRetentionMs;
    }

    public long getMaxRetentionMs() {
        return maxRetentionMs;
    }

    public void setMaxRetentionMs(long maxRetentionMs) {
        this.maxRetentionMs = maxRetentionMs;
    }
}
