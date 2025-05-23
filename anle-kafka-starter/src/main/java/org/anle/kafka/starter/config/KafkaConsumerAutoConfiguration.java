package org.anle.kafka.starter.config;

import org.anle.kafka.starter.properties.KafkaProperties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@ConditionalOnProperty(name = "kafka.consumer.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConsumerAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public KafkaConsumer<String, String> kafkaConsumer(KafkaProperties props) {
        return new KafkaConsumer<>(props.buildConsumerProperties());
    }
}
