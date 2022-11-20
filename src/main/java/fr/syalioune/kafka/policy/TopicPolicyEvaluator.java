package fr.syalioune.kafka.policy;

import fr.syalioune.kafka.policy.evaluator.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.AlterConfigPolicy;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Custom Kafka topic policy example.
 */
public class TopicPolicyEvaluator implements CreateTopicPolicy, AlterConfigPolicy {

    private static final Logger log = LoggerFactory.getLogger(TopicPolicyEvaluator.class);

    private Map<String, ?> configuration;

    private boolean enabled = false;

    private List<Pattern> topicExcludePatterns = Collections.emptyList();

    private Pattern topicNameGlobalPattern = Pattern.compile(".*");

    private List<TopicPolicy> policies = new LinkedList<>();

    private final List<PolicyEvaluator> policyEvaluators;

    public TopicPolicyEvaluator() {
        policyEvaluators = new LinkedList<>();
        policyEvaluators.add(new PartitionNumberEvaluator());
        policyEvaluators.add(new ReplicationFactorEvaluator());
        policyEvaluators.add(new IsrEvaluator());
        policyEvaluators.add(new RetentionEvaluator());
        policyEvaluators.add(new CleanupPolicyEvaluator());
    }

    @Override
    public void validate(AlterConfigPolicy.RequestMetadata requestMetadata) throws PolicyViolationException {
        if (requestMetadata.resource().type() == ConfigResource.Type.TOPIC) {
            log.debug("Evaluating topic "+requestMetadata.resource().name()+" against custom policies");
            validate(mapAlterMetadataToCreateMetadata(requestMetadata));
        }
    }

    @Override
    public void validate(CreateTopicPolicy.RequestMetadata requestMetadata) throws PolicyViolationException {
        if(enabled) {
            boolean shouldBypassPolicies = topicExcludePatterns.stream()
                    .map(pattern -> pattern.asMatchPredicate().test(requestMetadata.topic()))
                    .reduce(false, (a, b) -> a || b);
            if(!shouldBypassPolicies) {
                if(!topicNameGlobalPattern.asMatchPredicate().test(requestMetadata.topic())) {
                    String message = "The topic "+requestMetadata.topic()+" does not match the global topic name pattern "+ topicNameGlobalPattern.pattern();
                    throw new PolicyViolationException(message);
                }
                boolean matched = false;
                for (TopicPolicy policy : policies) {
                    log.debug("Checking topic "+requestMetadata.topic()+" against policy "+policy.getName());
                    if(policy.getPattern().asMatchPredicate().test(requestMetadata.topic())) {
                        log.debug("Topic "+requestMetadata.topic()+" will be evaluated against policy "+policy.getName());
                        policyEvaluators.forEach(policyEvaluator -> policyEvaluator.evaluate(requestMetadata, policy));
                        matched = true;
                        break;
                    } else {
                        log.debug("Topic "+requestMetadata.topic()+" should not be evaluated against policy "+policy.getName());
                    }
                }
                if(!matched) {
                    String message = "The topic "+requestMetadata.topic()+" does not match with any of the configured policy. Creation denied.";
                    throw new PolicyViolationException(message);
                }
            } else {
                log.debug("Policy bypass for topic "+requestMetadata.topic());
            }
        }
    }

    @Override
    public void close() throws Exception {
        log.debug("Closing topic policy evaluator");
    }

    @Override
    public void configure(Map<String, ?> map) {
        log.debug("Configuring topic policy evaluator");
        this.configuration = Collections.unmodifiableMap(map);
        String propertyValue;
        propertyValue = (String) configuration.get(TopicPolicyConfig.TOPIC_POLICY_ENABLE);
        if( propertyValue != null ) {
            this.enabled = Boolean.valueOf(propertyValue);
        }
        log.debug("Topic evaluator is "+(this.enabled ? "enabled" : "disabled"));
        propertyValue = (String) configuration.get(TopicPolicyConfig.TOPIC_POLICY_EXCLUDE_PATTERNS);
        if (propertyValue != null) {
            this.topicExcludePatterns = Arrays.stream(propertyValue.split(","))
                                .peek(pattern -> log.debug("Exclusion pattern detected : "+pattern))
                                .map(pattern -> Pattern.compile(pattern))
                                .collect(Collectors.toList());
        }
        propertyValue = (String) configuration.get(TopicPolicyConfig.TOPIC_POLICY_GLOBAL_TOPIC_NAME_PATTERN);
        if (propertyValue != null) {
            this.topicNameGlobalPattern = Pattern.compile(propertyValue);
        }
        log.debug("Global topic name pattern : "+this.topicNameGlobalPattern.pattern());
        propertyValue = (String) configuration.get(TopicPolicyConfig.TOPIC_POLICIES);
        if (propertyValue != null) {
            Arrays.stream(propertyValue.split(","))
                    .peek(policyName -> log.debug("Parsing policy "+policyName))
                    .map(policyName -> mapPolicy(policyName))
                    .forEach(policy -> this.policies.add(policy));
        }
        Collections.sort(this.policies, Comparator.comparingInt(TopicPolicy::getOrder));
    }

    private TopicPolicy mapPolicy(String policyName) {
        TopicPolicy policy = new TopicPolicy();
        policy.setName(policyName);
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_ORDER_SUFFIX, propertyValue -> policy.setOrder(Integer.parseInt(propertyValue)));
        log.debug("Priority for policy "+policyName+" is "+policy.getOrder());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_PATTERN_SUFFIX, propertyValue -> policy.setPattern(Pattern.compile(propertyValue)));
        log.debug("Pattern for policy "+policyName+" is "+policy.getPattern().pattern());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MIN_SUFFIX, propertyValue -> policy.setMinPartitions(Integer.valueOf(propertyValue)));
        log.debug("Minimum number of partitions for policy "+policyName+" is "+policy.getMinPartitions());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_PARTITIONS_MAX_SUFFIX, propertyValue -> policy.setMaxPartitions(Integer.valueOf(propertyValue)));
        log.debug("Maximum number of partitions for policy "+policyName+" is "+policy.getMaxPartitions());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MIN_SUFFIX, propertyValue -> policy.setMinReplicationFactor(Integer.valueOf(propertyValue)));
        log.debug("Minimum replication factor for policy "+policyName+" is "+policy.getMinReplicationFactor());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_REPLICATION_FACTOR_MAX_SUFFIX, propertyValue -> policy.setMaxReplicationFactor(Integer.valueOf(propertyValue)));
        log.debug("Maximum replication factor for policy "+policyName+" is "+policy.getMaxReplicationFactor());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_ISR_MIN_SUFFIX, propertyValue -> policy.setMinIsr(Integer.valueOf(propertyValue)));
        log.debug("Minimum ISR for policy "+policyName+" is "+policy.getMinIsr());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_ISR_MAX_SUFFIX, propertyValue -> policy.setMaxIsr(Integer.valueOf(propertyValue)));
        log.debug("Maximum ISR for policy "+policyName+" is "+policy.getMaxIsr());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MIN_SUFFIX, propertyValue -> policy.setMinRetentionMs(Long.valueOf(propertyValue)));
        log.debug("Minimum retention in ms for policy "+policyName+" is "+policy.getMinRetentionMs());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_RETENTION_MS_MAX_SUFFIX, propertyValue -> policy.setMaxRetentionMs(Long.valueOf(propertyValue)));
        log.debug("Maximum retention in ms for policy "+policyName+" is "+policy.getMaxRetentionMs());
        setPolicyPropertyValue(policyName, TopicPolicyConfig.TOPIC_POLICY_ALLOWED_CLEANUP_POLICIES_SUFFIX, propertyValue -> policy.setAllowedCleanupPolicies(
                Arrays.stream(propertyValue.split(","))
                        .peek(cleanupPolicy -> log.debug("Detecting policy "+cleanupPolicy))
                        .collect(Collectors.toList())
        ));
        return policy;
    }

    private void setPolicyPropertyValue(String policyName, String propertySuffix, Consumer<String> configureAction) {
        String propertyName = TopicPolicyConfig.TOPIC_POLICY_PREFIX+"."+policyName+"."+propertySuffix;
        String propertyValue = (String) configuration.get(propertyName);
        if (propertyValue != null) {
            configureAction.accept(propertyValue);
        }
    }

    private CreateTopicPolicy.RequestMetadata mapAlterMetadataToCreateMetadata(AlterConfigPolicy.RequestMetadata metadata) {
        String topic = metadata.resource().name();
        return new CreateTopicPolicy.RequestMetadata(topic, -1, (short) -1, Collections.emptyMap(), metadata.configs());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Pattern> getTopicExcludePatterns() {
        return topicExcludePatterns;
    }

    public Pattern getTopicNameGlobalPattern() {
        return topicNameGlobalPattern;
    }

    public List<TopicPolicy> getPolicies() {
        return policies;
    }

    public List<PolicyEvaluator> getPolicyEvaluators() {
        return policyEvaluators;
    }
}
