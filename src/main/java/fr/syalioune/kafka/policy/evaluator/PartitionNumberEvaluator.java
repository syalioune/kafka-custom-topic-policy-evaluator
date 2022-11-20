package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartitionNumberEvaluator implements PolicyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(PartitionNumberEvaluator.class);

    @Override
    public void evaluate(CreateTopicPolicy.RequestMetadata requestMetadata, TopicPolicy policy) throws PolicyViolationException {
        Integer numPartitions = requestMetadata.numPartitions();
        if (numPartitions == null) {
            numPartitions = requestMetadata.replicasAssignments().size();
        }
        log.debug("Configured number of partitions for topic "+requestMetadata.topic()+" is "+numPartitions);
        if(numPartitions != -1 && (policy.getMinPartitions() > numPartitions || numPartitions > policy.getMaxPartitions())) {
            String message = "The configured number of partitions ("+numPartitions+") for topic "
                    +requestMetadata.topic()+" does not fit in the allowed range ["+policy.getMinPartitions()+";"+policy.getMaxPartitions()+"] of policy "+policy.getName();
            throw new PolicyViolationException(message);
        }
    }
}
