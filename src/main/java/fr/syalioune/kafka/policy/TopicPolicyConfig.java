package fr.syalioune.kafka.policy;

import java.time.Duration;

/**
 * Configuration class for my custom topic policy example.
 */
public class TopicPolicyConfig {

    /**
     * Global prefix used for policy configuration.
     */
    public static final String TOPIC_POLICY_PREFIX = "topic.policy";

    /**
     * <code>topic.policy.enable</code>
     */
    public static final String TOPIC_POLICY_ENABLE = TOPIC_POLICY_PREFIX + ".enable";
    public static final String TOPIC_POLICY_ENABLE_DOC = "Flag to enable the custom policy evaluator or not."
                                                        +" Default value is false";

    /**
     * <code>topic.policy.exclude.patterns</code>
     */
    public static final String TOPIC_POLICY_EXCLUDE_PATTERNS = TOPIC_POLICY_PREFIX + ".exclude.patterns";
    public static final String TOPIC_POLICY_EXCLUDE_PATTERNS_DOC = "Comma separated list of regular expressions to match topic names which should not be evaluated by the policy evaluator.";

    /**
     * <code>topic.policy.global.topic.name.pattern</code>
     */
    public static final String TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN = TOPIC_POLICY_PREFIX + ".global.topic.name.pattern";
    public static final String TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN_DOC = "Global regular expression to check topic name validity."
                                                                +" Allow to perform a first level of validation before check specific policies.";

    /**
     * <code>topic.policies</code>
     */
    public static final String TOPIC_POLICIES = "topic.policies";
    public static final String TOPIC_POLICIES_DOC = "Comma separated list of policies to apply."
                                                    +" Once the topic policy evaluator is enabled, candidate topic should match one defined policy, otherwise the creation/modification will be denied."
                                                    +" The first policy pattern (by priority order) that match the topic name will be used."
                                                    +" It is advised to create a default policy with low priority as a fallback.";

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].order</code>
     */
    public static final String TOPIC_POLICY_ORDER_SUFFIX = "order";
    public static final String TOPIC_POLICY_ORDER_SUFFIX_DOC = "Order used to sort (ascending) the policy list."
                                                                +" The default value is INTEGER.MAX_VALUE."
                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+ TOPIC_POLICY_ORDER_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].pattern</code>
     */
    public static final String TOPIC_POLICY_PATTERN_SUFFIX = "pattern";
    public static final String TOPIC_POLICY_PATTERN_SUFFIX_DOC = "Topic name pattern to apply for this policy."
                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_PATTERN_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].partitions.min</code>
     */
    public static final String TOPIC_POLICY_PARTITIONS_MIN_SUFFIX = "partitions.min";
    public static final String TOPIC_POLICY_PARTITIONS_MIN_SUFFIX_DOC = "Minimum number of partitions for this policy."
                                                                        +" The default value is 0."
                                                                        +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_PARTITIONS_MIN_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].partitions.max</code>
     */
    public static final String TOPIC_POLICY_PARTITIONS_MAX_SUFFIX = "partitions.max";
    public static final String TOPIC_POLICY_PARTITIONS_MAX_SUFFIX_DOC = "Maximum number of partitions for this policy."
                                                                        +"The default value is Integer.MAX_VALUE."
                                                                        +"The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_PARTITIONS_MAX_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].replication.factor.min</code>
     */
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX = "replication.factor.min";
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX_DOC = "Minimum replication factor for this policy."
                                                                                +" The default value is 0."
                                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].partitions.max</code>
     */
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX = "replication.factor.max";
    public static final String TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX_DOC = "Maximum replication factor for this policy."
                                                                                +" The default value is Integer.MAX_VALUE."
                                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].isr.min</code>
     */
    public static final String TOPIC_POLICY_ISR_MIN_SUFFIX = "isr.min";
    public static final String TOPIC_POLICY_ISR_MIN_SUFFIX_DOC = "Minimum number of ISR for this policy."
                                                                +" The default value is 0."
                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_ISR_MIN_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].isr.max</code>
     */
    public static final String TOPIC_POLICY_ISR_MAX_SUFFIX = "isr.max";
    public static final String TOPIC_POLICY_ISR_MAX_SUFFIX_DOC = "Maximum number of ISR for this policy."
                                                                +" The default value is Integer.MAX_VALUE."
                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_ISR_MAX_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].allowed.cleanup.policies</code>
     */
    public static final String TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX = "allowed.cleanup.policies";
    public static final String TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX_DOC = "Comma separated list of allowed cleanup policies."
                                                                                +" The default value is delete,compact."
                                                                                +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].retention.ms.min</code>
     */
    public static final String TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX = "retention.ms.min";
    public static final String TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX_DOC = "Minimum retention duration in ms for this policy."
                                                                        +" The default value is "+Duration.ofHours(1).toMillis()+"ms."
                                                                        +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX;

    /**
     * <code>$TOPIC_POLICY_PREFIX.$TOPIC_POLICIES[*].retention.ms.max</code>
     */
    public static final String TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX = "retention.ms.max";
    public static final String TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX_DOC = "Maximum retention duration in ms for this policy."
                                                                        +" The default value is Long.MAX_VALUE."
                                                                        +" The property for a policy named 'test' will be "+TOPIC_POLICY_PREFIX+".test."+TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX;

    public static void generateSampleProperties() {
        StringBuilder builder = new StringBuilder();
        builder.append("#Policy declaration\n");
        builder.append("create.topic.policy.class.name=").append(TopicPolicyEvaluator.class.getName()).append("\n");
        builder.append("alter.config.policy.class.name=").append(TopicPolicyEvaluator.class.getName()).append("\n");
        builder.append("\n");
        builder.append("#").append(TOPIC_POLICY_ENABLE_DOC).append("\n");
        builder.append(TOPIC_POLICY_ENABLE).append("=").append("true").append("\n");
        builder.append("#").append(TOPIC_POLICY_EXCLUDE_PATTERNS_DOC).append("\n");
        builder.append(TOPIC_POLICY_EXCLUDE_PATTERNS).append("=").append("^__.*,myTopic").append("\n");
        builder.append("#").append(TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN_DOC).append("\n");
        builder.append(TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN).append("=").append(".*").append("\n");
        builder.append("#").append(TOPIC_POLICIES_DOC).append("\n");
        builder.append(TOPIC_POLICIES).append("=").append("default,domain1").append("\n");
        builder.append("\n");
        generatePolicySample(builder,"domain1", 1);
        generatePolicySample(builder, "default", Integer.MAX_VALUE);
        System.out.println(builder.toString());
    }

    public static void generatePolicySample(StringBuilder builder, String policyName, int order) {
        builder.append("#").append(TOPIC_POLICY_PATTERN_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_PATTERN_SUFFIX).append("=").append(".*").append("\n");
        builder.append("#").append(TOPIC_POLICY_ORDER_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_ORDER_SUFFIX).append("=").append(order).append("\n");
        builder.append("#").append(TOPIC_POLICY_PARTITIONS_MIN_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_PARTITIONS_MIN_SUFFIX).append("=").append("2").append("\n");
        builder.append("#").append(TOPIC_POLICY_PARTITIONS_MAX_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_PARTITIONS_MAX_SUFFIX).append("=").append("5").append("\n");
        builder.append("#").append(TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX).append("=").append("2").append("\n");
        builder.append("#").append(TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX).append("=").append("5").append("\n");
        builder.append("#").append(TOPIC_POLICY_ISR_MIN_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_ISR_MIN_SUFFIX).append("=").append("2").append("\n");
        builder.append("#").append(TOPIC_POLICY_ISR_MAX_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_ISR_MAX_SUFFIX).append("=").append("5").append("\n");
        builder.append("#").append(TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX).append("=").append("0").append("\n");
        builder.append("#").append(TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX).append("=").append(Duration.ofDays(7).toMillis()).append("\n");
        builder.append("#").append(TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX_DOC).append("\n");
        builder.append(TOPIC_POLICY_PREFIX).append(".").append(policyName).append(".").append(TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX).append("=").append("delete,compact").append("\n");
        builder.append("\n");
    }
}
