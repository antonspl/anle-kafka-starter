package org.anle.kafka.starter.config;

import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.impl.AvroMessageProducer;
import org.anle.kafka.starter.ifc.MessageProducerWrapper;
import org.anle.kafka.starter.properties.ProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@AutoConfiguration
@ConditionalOnProperty(name = "anle.kafka.producer.value-serializer", havingValue = "io.confluent.kafka.serializers.KafkaAvroSerializer")
@EnableConfigurationProperties(ProducerProperties.class)
@ConditionalOnMissingBean(MessageProducerWrapper.class)
public class AvroProducerConfig {

    @Bean(destroyMethod = "close")
    public KafkaProducer<String, NumberCreated> kafkaProducer(ProducerProperties producerProps) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProps.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProps.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProps.getValueSerializer());
        props.put("schema.registry.url", producerProps.getSchemaRegistryUrl());
        return new KafkaProducer<>(props);
    }

    @Bean
    public MessageProducerWrapper<NumberCreated> producer(KafkaProducer<String, NumberCreated> kafkaProducer) {
        return new AvroMessageProducer(kafkaProducer);
    }
}
