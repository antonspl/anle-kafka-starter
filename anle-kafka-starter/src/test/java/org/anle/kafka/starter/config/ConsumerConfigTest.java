package org.anle.kafka.starter.config;

import org.anle.kafka.starter.impl.AvroMessageConsumer;
import org.anle.kafka.starter.impl.StringMessageConsumer;
import org.anle.kafka.starter.properties.ConsumerProperties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ConsumerConfigTest {

    @Test
    void stringConsumerBeanIsCreated() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringConsumerConfig.class))
                .withPropertyValues(
                        "anle.kafka.consumer.bootstrap-servers=localhost:9092",
                        "anle.kafka.consumer.group-id=test-group",
                        "anle.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.enable-auto-commit=true",
                        "anle.kafka.consumer.offset-reset=earliest"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(StringMessageConsumer.class);
                    assertThat(context).doesNotHaveBean(AvroMessageConsumer.class);
                    assertThat(context).hasSingleBean(ConsumerProperties.class);
                });
    }

    @Test
    void avroConsumerBeanIsCreated() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(AvroConsumerConfig.class))
                .withPropertyValues(
                        "anle.kafka.consumer.bootstrap-servers=localhost:9092",
                        "anle.kafka.consumer.group-id=test-group",
                        "anle.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.value-deserializer=io.confluent.kafka.serializers.KafkaAvroDeserializer",
                        "anle.kafka.consumer.enable-auto-commit=true",
                        "anle.kafka.consumer.schema-registry-url=http://localhost:8081",
                        "anle.kafka.consumer.offset-reset=earliest"
                )
                .run(context -> {
                    assertThat(context).doesNotHaveBean(StringMessageConsumer.class);
                    assertThat(context).hasSingleBean(AvroMessageConsumer.class);
                    assertThat(context).hasSingleBean(ConsumerProperties.class);
                });
    }

    @Test
    void consumerBeanIsNotCreatedWhenDisabled() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringConsumerConfig.class))
                .withPropertyValues("anle.kafka.consumer.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(KafkaConsumer.class);
                });
    }

    @Test
    void consumerBeanFailsWithoutRequiredProperties() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringConsumerConfig.class))
                .withPropertyValues("anle.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer")
                .run(context -> {
                    assertThat(context.getStartupFailure())
                            .isInstanceOf(BeanCreationException.class)
                            .hasRootCauseInstanceOf(NullPointerException.class);
                });
    }

    @Test
    void consumerBeanIsClosedOnContextShutdown() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringConsumerConfig.class))
                .withPropertyValues(
                        "anle.kafka.consumer.bootstrap-servers=localhost:9092",
                        "anle.kafka.consumer.group-id=test-group",
                        "anle.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.enable-auto-commit=true",
                        "anle.kafka.consumer.offset-reset=earliest"
                )
                .run(context -> {
                    KafkaConsumer<?, ?> consumer = context.getBean(KafkaConsumer.class);
                    assertThat(consumer).isNotNull();
                    context.close(); // no exception   thrown
                });
    }
}
