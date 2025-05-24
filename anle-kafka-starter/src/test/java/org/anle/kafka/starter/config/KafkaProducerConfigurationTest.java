package org.anle.kafka.starter.config;

import org.anle.kafka.starter.properties.KafkaProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaProducerConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaProducerConfiguration.class));

    @Test
    void producerBeanIsCreatedWhenEnabled() {
        contextRunner
                .withPropertyValues(
                        "anle.kafka.producer.enabled=true",
                        "anle.kafka.producer.bootstrap-servers=localhost:9092",
                        "anle.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                        "anle.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(KafkaProducer.class);
                    assertThat(context).hasSingleBean(KafkaProducerProperties.class);
                });
    }

    @Test
    void producerBeanIsNotCreatedWhenDisabled() {
        contextRunner
                .withPropertyValues("anle.kafka.producer.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(KafkaProducer.class);
                });
    }

    @Test
    void producerBeanFailsWithoutRequiredProperties() {
        contextRunner
                .withPropertyValues("anle.kafka.producer.enabled=true")
                .run(context -> {
                    assertThat(context.getStartupFailure())
                            .isInstanceOf(BeanCreationException.class)
                            .hasRootCauseInstanceOf(NullPointerException.class);
                });
    }

    @Test
    void producerBeanIsClosedOnContextShutdown() {
        contextRunner
                .withPropertyValues(
                        "anle.kafka.producer.enabled=true",
                        "anle.kafka.producer.bootstrap-servers=localhost:9092",
                        "anle.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                        "anle.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
                )
                .run(context -> {
                    KafkaProducer<?, ?> producer = context.getBean(KafkaProducer.class);
                    assertThat(producer).isNotNull();
                    context.close(); // no exception thrown
                });
    }
}