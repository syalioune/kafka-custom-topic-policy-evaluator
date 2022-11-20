# Kafka topic policy

The purpose of this repository is to provide a pluggable and configurable topic policy rule enforcer for [Apache Kafka](https://kafka.apache.org/) as described [here](https://kafka.apache.org/documentation/#brokerconfigs_create.topic.policy.class.name).

## Prerequisites

* Java 8+
* Kafka 2.6+

## Build from source

```
mvn clean package
```

The artifact should be available in `./target/kafka-custom-topic-policy-x.y.z.jar`

## Usage

1. Put the jar file in `$KAFKA_HOME/libs` directory

## Configuration

```properties
#Policy declaration
create.topic.policy.class.name=fr.syalioune.kafka.policy.TopicPolicyEvaluator
alter.config.policy.class.name=fr.syalioune.kafka.policy.TopicPolicyEvaluator

#Flag to enable the custom policy evaluator or not. Default value is false
topic.policy.enable=true
#Comma separated list of regular expressions to match topic names which should not be evaluated by the policy evaluator.
topic.policy.exclude.patterns=^__.*,myTopic
#Global regular expression to check topic name validity. Allow to perform a first level of validation before check specific policies.
topic.policy.global.topic.name.pattern=.*
#Comma separated list of policies to apply. Once the topic policy evaluator is enabled, candidate topic should match one defined policy, otherwise the creation/modification will be denied. The first policy pattern (by priority order) that match the topic name will be used. It is advised to create a default policy with low priority as a fallback.
topic.policies=default,domain1

#Topic name pattern to apply for this policy. The property for a policy named 'test' will be topic.policy.test.pattern
topic.policy.domain1.pattern=.*
#Order used to sort (ascending) the policy list. The default value is INTEGER.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.order
topic.policy.domain1.order=1
#Minimum number of partitions for this policy. The default value is 0. The property for a policy named 'test' will be topic.policy.test.partitions.min
topic.policy.domain1.partitions.min=2
#Maximum number of partitions for this policy.The default value is Integer.MAX_VALUE.The property for a policy named 'test' will be topic.policy.test.partitions.max
topic.policy.domain1.partitions.max=5
#Minimum replication factor for this policy. The default value is 0. The property for a policy named 'test' will be topic.policy.test.replication.factor.min
topic.policy.domain1.replication.factor.min=2
#Maximum replication factor for this policy. The default value is Integer.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.replication.factor.max
topic.policy.domain1.replication.factor.max=5
#Minimum number of ISR for this policy. The default value is 0. The property for a policy named 'test' will be topic.policy.test.isr.min
topic.policy.domain1.isr.min=2
#Maximum number of ISR for this policy. The default value is Integer.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.isr.max
topic.policy.domain1.isr.max=5
#Minimum retention duration in ms for this policy. The default value is 3600000ms. The property for a policy named 'test' will be topic.policy.test.retention.ms.min
topic.policy.domain1.retention.ms.min=0
#Maximum retention duration in ms for this policy. The default value is Long.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.retention.ms.max
topic.policy.domain1.retention.ms.max=604800000
#Comma separated list of allowed cleanup policie. The default value is delete,compact. The property for a policy named 'test' will be topic.policy.test.allowed.cleanup.policies
topic.policy.domain1.allowed.cleanup.policies=delete,compact

#Topic name pattern to apply for this policy. The property for a policy named 'test' will be topic.policy.test.pattern
topic.policy.default.pattern=.*
#Order used to sort (ascending) the policy list. The default value is INTEGER.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.order
topic.policy.default.order=2147483647
#Minimum number of partitions for this policy. The default value is 0. The property for a policy named 'test' will be topic.policy.test.partitions.min
topic.policy.default.partitions.min=2
#Maximum number of partitions for this policy.The default value is Integer.MAX_VALUE.The property for a policy named 'test' will be topic.policy.test.partitions.max
topic.policy.default.partitions.max=5
#Minimum replication factor for this policy. The default value is 0. The property for a policy named 'test' will be topic.policy.test.replication.factor.min
topic.policy.default.replication.factor.min=2
#Maximum replication factor for this policy. The default value is Integer.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.replication.factor.max
topic.policy.default.replication.factor.max=5
#Minimum number of ISR for this policy. The default value is 0. The property for a policy named 'test' will be topic.policy.test.isr.min
topic.policy.default.isr.min=2
#Maximum number of ISR for this policy. The default value is Integer.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.isr.max
topic.policy.default.isr.max=5
#Minimum retention duration in ms for this policy. The default value is 3600000ms. The property for a policy named 'test' will be topic.policy.test.retention.ms.min
topic.policy.default.retention.ms.min=0
#Maximum retention duration in ms for this policy. The default value is Long.MAX_VALUE. The property for a policy named 'test' will be topic.policy.test.retention.ms.max
topic.policy.default.retention.ms.max=604800000
#Comma separated list of allowed cleanup policies. The default value is delete,compact. The property for a policy named 'test' will be topic.policy.test.allowed.cleanup.policies
topic.policy.default.allowed.cleanup.policies=delete,compact
```

## Limits

* The policy are not called when topics are created automatically with [allow.auto.create.topics](https://kafka.apache.org/documentation/#consumerconfigs_allow.auto.create.topics)
* The **partition number** and **replication factor** are not available on Topic modification and are thus not checked. See [KIP-201](https://cwiki.apache.org/confluence/display/KAFKA/KIP-201%3A+Rationalising+Policy+interfaces) 
