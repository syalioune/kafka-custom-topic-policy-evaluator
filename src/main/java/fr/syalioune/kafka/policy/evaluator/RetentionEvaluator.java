package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetentionEvaluator implements PolicyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RetentionEvaluator.class);

    @Override
    public void evaluate(CreateTopicPolicy.RequestMetadata requestMetadata, TopicPolicy policy) throws PolicyViolationException {
        String retentionMsProperty = requestMetadata.configs().get(TopicConfig.RETENTION_MS_CONFIG);
        Long retentionMs = -1L;
        if(retentionMsProperty != null) {
            retentionMs = Long.valueOf(retentionMsProperty);
        }
        log.debug("Configured retention for topic "+requestMetadata.topic()+" is "+retentionMs+"ms");
        if(retentionMs != -1 && (policy.getMinRetentionMs() > retentionMs || retentionMs > policy.getMaxRetentionMs())) {
            String message = "The configured retention ("+retentionMs+"ms) for topic "
                    +requestMetadata.topic()+" does not fit in the allowed range ["+policy.getMinRetentionMs()+";"+policy.getMaxRetentionMs()+"] of policy "+policy.getName();
            throw new PolicyViolationException(message);
        }
    }
}
