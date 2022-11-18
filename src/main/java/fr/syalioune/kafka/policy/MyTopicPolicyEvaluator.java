package fr.syalioune.kafka.policy;

import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.AlterConfigPolicy;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Custom Kafka topic policy example.
 */
public class MyTopicPolicyEvaluator implements CreateTopicPolicy, AlterConfigPolicy {

    private static final Logger log = LoggerFactory.getLogger(MyTopicPolicyEvaluator.class);

    private Map<String, ?> configuration;

    private boolean enabled = false;

    private List<Pattern> topicExcludes = Collections.emptyList();

    private Pattern topicNameRegexp = Pattern.compile(".*");

    private Map<String, MyTopicPolicy> policies = new HashMap<>();

    @Override
    public void validate(AlterConfigPolicy.RequestMetadata requestMetadata) throws PolicyViolationException {

    }

    @Override
    public void validate(CreateTopicPolicy.RequestMetadata requestMetadata) throws PolicyViolationException {

    }

    @Override
    public void close() throws Exception {
        log.debug("Closing my custom topic policy evaluator");
    }

    @Override
    public void configure(Map<String, ?> map) {
        log.debug("Configuring my custom topic policy evaluator");
        this.configuration = Collections.unmodifiableMap(map);
        String propertyValue;
        propertyValue = (String) configuration.get(MyTopicPolicyConfig.TOPIC_POLICY_ENABLE);
        if( propertyValue != null ) {
            this.enabled = Boolean.valueOf(propertyValue);
        }
        log.debug("UCN topic evaluator is "+(this.enabled ? "enabled" : "disabled"));
        propertyValue = (String) configuration.get(MyTopicPolicyConfig.TOPIC_POLICY_EXCLUDE_REGEXP_LIST);
        if (propertyValue != null) {
            this.topicExcludes = Arrays.stream(propertyValue.split(","))
                                .peek(pattern -> log.debug("Exclusion pattern detected : "+pattern))
                                .map(pattern -> Pattern.compile(pattern))
                                .collect(Collectors.toList());
        }
        propertyValue = (String) configuration.get(MyTopicPolicyConfig.TOPIC_POLICY_GLOBAL_TOPIC_NAME_REGEXP);
        if (propertyValue != null) {
            this.topicNameRegexp = Pattern.compile(propertyValue);
        }
        log.debug("Global topic name regexp : "+this.topicNameRegexp.pattern());
        propertyValue = (String) configuration.get(MyTopicPolicyConfig.TOPIC_POLICIES);
        if (propertyValue != null) {
            Arrays.stream(propertyValue.split(","))
                    .peek(policyName -> log.debug("Parsing policy "+policyName))
                    .map(policyName -> getPolicy(policyName, this.configuration))
                    .forEach(policy -> this.policies.put(policy.getName(), policy));
        }
    }

    private MyTopicPolicy getPolicy(String policyName, Map<String, ?> configuration) {
        MyTopicPolicy policy = new MyTopicPolicy();
        String propertyValue;
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_REGEXP_SUFFIX));
        if (propertyValue != null) {
            policy.setRegexp(Pattern.compile(propertyValue));
        }
        log.debug("Pattern for policy "+policyName+" is "+policy.getRegexp().pattern());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MIN_SUFFIX));
        if (propertyValue != null) {
            policy.setMinPartitions(Integer.valueOf(propertyValue));
        }
        log.debug("Minimum number of partitions for policy "+policyName+" is "+policy.getMinPartitions());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MAX_SUFFIX));
        if (propertyValue != null) {
            policy.setMaxPartitions(Integer.valueOf(propertyValue));
        }
        log.debug("Maximum number of partitions for policy "+policyName+" is "+policy.getMaxPartitions());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX));
        if (propertyValue != null) {
            policy.setMinReplicationFactor(Integer.valueOf(propertyValue));
        }
        log.debug("Minimum replication factor for policy "+policyName+" is "+policy.getMinReplicationFactor());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX));
        if (propertyValue != null) {
            policy.setMaxReplicationFactor(Integer.valueOf(propertyValue));
        }
        log.debug("Maximum replication factor for policy "+policyName+" is "+policy.getMaxReplicationFactor());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_ISR_MIN_SUFFIX));
        if (propertyValue != null) {
            policy.setMinIsr(Integer.valueOf(propertyValue));
        }
        log.debug("Minimum ISR for policy "+policyName+" is "+policy.getMinIsr());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_ISR_MAX_SUFFIX));
        if (propertyValue != null) {
            policy.setMaxIsr(Integer.valueOf(propertyValue));
        }
        log.debug("Maximum ISR for policy "+policyName+" is "+policy.getMaxIsr());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX));
        if (propertyValue != null) {
            policy.setMinRetentionMs(Long.valueOf(propertyValue));
        }
        log.debug("Minimum retention in ms for policy "+policyName+" is "+policy.getMinRetentionMs());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX));
        if (propertyValue != null) {
            policy.setMaxRetentionMs(Integer.valueOf(propertyValue));
        }
        log.debug("Maximum retention in ms for policy "+policyName+" is "+policy.getMaxRetentionMs());
        propertyValue = (String) configuration.get(getPolicyPropertyName(policyName, MyTopicPolicyConfig.TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX));
        if (propertyValue != null) {
            policy.setAllowedCleanupPolicies(
                    Arrays.stream(propertyValue.split(","))
                            .peek(cleanupPolicy -> log.debug("Detecting policy "+cleanupPolicy))
                            .collect(Collectors.toList())
            );
        }
        return policy;
    }

    private String getPolicyPropertyName(String policyName, String suffix) {
        return MyTopicPolicyConfig.TOPIC_POLICY_PREFIX+"."+policyName+"."+suffix;
    }
}
