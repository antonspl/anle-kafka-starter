package org.anle.kafka.starter.config;

import org.anle.kafka.starter.properties.KafkaProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;


@AutoConfiguration
@ConditionalOnProperty(name = "anle.kafka.producer.enabled", havingValue = "true")
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public KafkaProducer<String, String> kafkaProducer(KafkaProducerProperties producerProps) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProps.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProps.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProps.getValueSerializer());
        return new KafkaProducer<>(props);
    }
}
