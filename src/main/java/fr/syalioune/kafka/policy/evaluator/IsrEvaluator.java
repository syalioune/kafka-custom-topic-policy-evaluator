package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsrEvaluator implements PolicyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(IsrEvaluator.class);

    @Override
    public void evaluate(CreateTopicPolicy.RequestMetadata requestMetadata, TopicPolicy policy) throws PolicyViolationException {
        String isrProperty = requestMetadata.configs().get(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG);
        short isr = -1;
        if (isrProperty != null) {
            isr = Short.valueOf(isrProperty);
        }
        log.debug("Configured min.insync.replicas for topic "+requestMetadata.topic()+" is "+isr);
        if(isr != -1 && (policy.getMinIsr() > isr || isr > policy.getMaxIsr())) {
            String message = "The configured min.insync.replicas ("+isr+") for topic "
                    +requestMetadata.topic()+" does not fit in the allowed range ["+policy.getMinIsr()+";"+policy.getMaxIsr()+"] of policy "+policy.getName();
            throw new PolicyViolationException(message);
        }
    }
}
