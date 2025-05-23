package org.anle.kafka.starter.config;

import org.anle.kafka.starter.properties.KafkaProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@ConditionalOnProperty(name = "kafka.producer.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProducerAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public Producer<String, String> kafkaProducer(KafkaProperties props) {
        return new KafkaProducer<>(props.buildProducerProperties());
    }
}
