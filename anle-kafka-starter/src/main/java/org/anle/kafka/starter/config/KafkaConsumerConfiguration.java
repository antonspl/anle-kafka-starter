package org.anle.kafka.starter.config;

import org.anle.kafka.starter.properties.KafkaConsumerProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;


@AutoConfiguration
@ConditionalOnProperty(name = "anle.kafka.consumer.enabled", havingValue = "true")
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public KafkaConsumer<String, String> kafkaConsumer(KafkaConsumerProperties consumerProps) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProps.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProps.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProps.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProps.getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProps.getEnableAutoCommit());

        return new KafkaConsumer<>(props);
    }
}
