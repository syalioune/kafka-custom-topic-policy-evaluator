package fr.syalioune.kafka.policy;

/**
 * Configuration class for my custom topic policy example.
 */
public class MyTopicPolicyConfig {

    /**
     * Global prefix used for policy configuration.
     */
    public static final String TOPIC_POLICY_PREFIX = "topic.policy";

    /**
     * <code>topic.policy.enable</code>
     */
    public static final String TOPIC_POLICY_ENABLE = TOPIC_POLICY_PREFIX + ".enable";
    public static final String TOPIC_POLICY_ENABLE_DOC = "Flag to enable the custom policy or not.";

    /**
     * <code>topic.policy.exclude.list</code>
     */
    public static final String TOPIC_POLICY_EXCLUDE_REGEXP_LIST = TOPIC_POLICY_PREFIX + ".exclude.regexp.list";
    public static final String TOPIC_POLICY_EXCLUDE_REGEXP_LIST_DOC = "Comma separated list of regular expressions for topic names which should not be evaluated by the policy.";

    /**
     * <code>topic.policy.global.topic.name.regexp</code>
     */
    public static final String TOPIC_POLICY_GLOBAL_TOPIC_NAME_REGEXP = TOPIC_POLICY_PREFIX + ".global.topic.name.regexp";
    public static final String TOPIC_POLICY_GLOBAL_REGEXP_DOC = "Global regular expression to check topic name validity.";

    /**
     * <code>topic.policies</code>
     */
    public static final String TOPIC_POLICIES = "topic.policies";
    public static final String TOPIC_POLICIES_DOC = "Comma separated list of policies to apply.";


    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].regexp</code>
     */
    public static final String TOPIC_POLICY_REGEXP_SUFFIX = "regexp";
    public static final String TOPIC_POLICY_REGEXP_SUFFIX_DOC = "Topic name regexp to apply for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].partitions.min</code>
     */
    public static final String TOPIC_POLICY_PARTITIONS_MIN_SUFFIX = "partitions.min";
    public static final String TOPIC_POLICY_PARTITIONS_MIN_SUFFIX_DOC = "Minimum number of partitions for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].partitions.max</code>
     */
    public static final String TOPIC_POLICY_PARTITIONS_MAX_SUFFIX = "partitions.max";
    public static final String TOPIC_POLICY_PARTITIONS_MAX_SUFFIX_DOC = "Maximum number of partitions for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].replication.factor.min</code>
     */
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX = "replication.factor.min";
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX_DOC = "Minimum number of replication factor for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].partitions.max</code>
     */
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX = "replication.factor.max";
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX_DOC = "Maximum number of replication factor for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].isr.min</code>
     */
    public static final String TOPIC_POLICY_ISR_MIN_SUFFIX = "isr.min";
    public static final String TOPIC_POLICY_ISR_MIN_SUFFIX_DOC = "Minimum number of ISR for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].isr.max</code>
     */
    public static final String TOPIC_POLICY_ISR_MAX_SUFFIX = "isr.max";
    public static final String TOPIC_POLICY_ISR_MAX_SUFFIX_DOC = "Maximum number of ISR for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].allowed.cleanup.policies</code>
     */
    public static final String TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX = "allowed.cleanup.policies";
    public static final String TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX_DOC = "Comma separated list of allowed cleanup policies (i.e. compact, delete).";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].retention.ms.min</code>
     */
    public static final String TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX = "retention.ms.min";
    public static final String TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX_DOC = "Minimum retention duration in ms for this policy.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].retention.ms.max</code>
     */
    public static final String TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX = "retention.ms.max";
    public static final String TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX_DOC = "Maximum retention duration in ms for this policy.";
}
