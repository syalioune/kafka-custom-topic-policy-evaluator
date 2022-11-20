package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanupPolicyEvaluator implements PolicyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(CleanupPolicyEvaluator.class);

    @Override
    public void evaluate(CreateTopicPolicy.RequestMetadata requestMetadata, TopicPolicy policy) throws PolicyViolationException {
        String cleanupPolicy = requestMetadata.configs().get(TopicConfig.CLEANUP_POLICY_CONFIG);
        if(cleanupPolicy != null && !policy.getAllowedCleanupPolicies().contains(cleanupPolicy)) {
            log.debug("Configured cleanup policy for topic "+requestMetadata.topic()+" is "+cleanupPolicy);
            String message = "The configured cleanup policy ("+cleanupPolicy+") for topic "
                    +requestMetadata.topic()+" is not allowed by policy "+policy.getName();
            throw new PolicyViolationException(message);
        }
    }
}
