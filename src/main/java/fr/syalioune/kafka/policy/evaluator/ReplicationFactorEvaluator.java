package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicationFactorEvaluator implements PolicyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(ReplicationFactorEvaluator.class);

    @Override
    public void evaluate(CreateTopicPolicy.RequestMetadata requestMetadata, TopicPolicy policy) throws PolicyViolationException {
        Short replicationFactor = requestMetadata.replicationFactor();
        if (replicationFactor == null) {
            replicationFactor = (short) requestMetadata.replicasAssignments().get(0).size();
        }
        log.debug("Configured replication factor for topic "+requestMetadata.topic()+" is "+replicationFactor);
        if(replicationFactor != -1 && (policy.getMinReplicationFactor() > replicationFactor || replicationFactor > policy.getMaxReplicationFactor())) {
            String message = "The configured replication factor ("+replicationFactor+") for topic "
                    +requestMetadata.topic()+" does not fit in the allowed range ["+policy.getMinReplicationFactor()+";"+policy.getMaxReplicationFactor()+"] of policy "+policy.getName();
            throw new PolicyViolationException(message);
        }
    }
}
