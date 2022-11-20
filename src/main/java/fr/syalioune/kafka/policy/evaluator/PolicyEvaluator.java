package fr.syalioune.kafka.policy.evaluator;

import fr.syalioune.kafka.policy.TopicPolicy;
import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;

public interface PolicyEvaluator {

    void evaluate(CreateTopicPolicy.RequestMetadata requestMetadata, TopicPolicy policy) throws PolicyViolationException;

}
