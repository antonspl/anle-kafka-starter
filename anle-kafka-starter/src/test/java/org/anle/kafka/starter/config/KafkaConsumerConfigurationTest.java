package org.anle.kafka.starter.config;

import org.anle.kafka.starter.properties.KafkaConsumerProperties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConsumerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaConsumerConfiguration.class));
    

    @Test
    void consumerBeanIsCreated() {
        contextRunner
                .withPropertyValues(
                        "anle.kafka.consumer.enabled=true",
                        "anle.kafka.consumer.bootstrap-servers=localhost:9092",
                        "anle.kafka.consumer.group-id=test-group",
                        "anle.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                        "anle.kafka.consumer.enable-auto-commit=true",
                        "anle.kafka.consumer.offset-reset=earliest"
                )
                .run(context -> {
            assertThat(context).hasSingleBean(KafkaConsumer.class);
            assertThat(context).hasSingleBean(KafkaConsumerProperties.class);
        });
    }

    @Test
    void consumerBeanIsNotCreatedWhenDisabled() {
        contextRunner
                .withPropertyValues("anle.kafka.consumer.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(KafkaConsumer.class);
                });
    }

    @Test
    void consumerBeanFailsWithoutRequiredProperties() {
        contextRunner
                .withPropertyValues("anle.kafka.consumer.enabled=true")
                .run(context -> {
                    assertThat(context.getStartupFailure())
                            .isInstanceOf(BeanCreationException.class)
                            .hasRootCauseInstanceOf(NullPointerException.class);
                });
    }

    @Test
    void consumerBeanIsClosedOnContextShutdown() {
        contextRunner
                .withPropertyValues(
                        "anle.kafka.consumer.enabled=true",
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
                    context.close(); // no exception thrown
                });
    }
}