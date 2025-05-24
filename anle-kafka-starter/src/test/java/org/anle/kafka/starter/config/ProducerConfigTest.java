package org.anle.kafka.starter.config;

import org.anle.kafka.starter.impl.AvroMessageProducer;
import org.anle.kafka.starter.impl.StringMessageProducer;
import org.anle.kafka.starter.properties.ProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ProducerConfigTest {

    @Test
    void stringProducerBeanIsCreated() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringProducerConfig.class))
                .withPropertyValues(
                        "anle.kafka.producer.bootstrap-servers=localhost:9092",
                        "anle.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                        "anle.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(StringMessageProducer.class);
                    assertThat(context).doesNotHaveBean(AvroMessageProducer.class);
                    assertThat(context).hasSingleBean(ProducerProperties.class);
                });
    }

    @Test
    void avroProducerBeanIsCreated() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(AvroProducerConfig.class))
                .withPropertyValues(
                        "anle.kafka.producer.bootstrap-servers=localhost:9092",
                        "anle.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                        "anle.kafka.producer.value-serializer=io.confluent.kafka.serializers.KafkaAvroSerializer",
                        "anle.kafka.producer.schema-registry-url=http://localhost:8081"
                )
                .run(context -> {
                    assertThat(context).doesNotHaveBean(StringMessageProducer.class);
                    assertThat(context).hasSingleBean(AvroMessageProducer.class);
                    assertThat(context).hasSingleBean(ProducerProperties.class);
                });
    }

    @Test
    void producerBeanIsNotCreated() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringProducerConfig.class))
                .withPropertyValues("anle.kafka.producer.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(KafkaProducer.class);
                });
    }

    @Test
    void producerBeanFailsWithoutRequiredProperties() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringProducerConfig.class))
                .withPropertyValues("anle.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer")
                .run(context -> {
                    assertThat(context.getStartupFailure())
                            .isInstanceOf(BeanCreationException.class)
                            .hasRootCauseInstanceOf(NullPointerException.class);
                });
    }

    @Test
    void producerBeanIsClosedOnContextShutdown() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(StringProducerConfig.class))
                .withPropertyValues(
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
