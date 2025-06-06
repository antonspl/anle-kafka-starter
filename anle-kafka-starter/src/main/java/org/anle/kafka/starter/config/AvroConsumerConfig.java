package org.anle.kafka.starter.config;

import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.ifc.MessageConsumer;
import org.anle.kafka.starter.impl.AvroMessageConsumer;
import org.anle.kafka.starter.properties.ConsumerProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@AutoConfiguration
@ConditionalOnProperty(name = "anle.kafka.consumer.value-deserializer", havingValue = "io.confluent.kafka.serializers.KafkaAvroDeserializer")
@EnableConfigurationProperties(ConsumerProperties.class)
@ConditionalOnMissingBean(MessageConsumer.class)
public class AvroConsumerConfig {

    @Bean(destroyMethod = "close")
    public KafkaConsumer<String, NumberCreated> kafkaAvroConsumer(ConsumerProperties consumerProps) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProps.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProps.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProps.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProps.getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProps.getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProps.getOffsetReset());
        props.put("schema.registry.url", consumerProps.getSchemaRegistryUrl());
        props.put("specific.avro.reader", "true");
        return new KafkaConsumer<>(props);
    }

    @Bean
    public MessageConsumer<NumberCreated> consumer(KafkaConsumer<String, NumberCreated> kafkaConsumer) {
        return new AvroMessageConsumer(kafkaConsumer);
    }
}
