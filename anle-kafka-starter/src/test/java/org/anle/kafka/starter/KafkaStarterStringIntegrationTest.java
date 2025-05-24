package org.anle.kafka.starter;

import org.anle.kafka.starter.config.StringConsumerConfig;
import org.anle.kafka.starter.config.StringProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(
        classes = {
                StringProducerConfig.class,
                StringConsumerConfig.class
        },
        properties = {
                "anle.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "anle.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "anle.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",

                "anle.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "anle.kafka.consumer.group-id=test-group",
                "anle.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                "anle.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                "anle.kafka.consumer.enable-auto-commit=true",
                "anle.kafka.consumer.offset-reset=earliest"
        }
)
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {"replication.factor=1", "min.insync.replicas=1"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaStarterStringIntegrationTest {

    private static final String TOPIC = "test-topic";

    @Autowired
    private KafkaProducer<String, String> producer;

    @Autowired
    private KafkaConsumer<String, String> consumer;

    @Autowired
    private Environment env;

    @AfterAll
    void tearDown() {
        producer.close();
        consumer.close();
    }

    @BeforeAll
    void setup() {
        consumer.subscribe(Collections.singletonList(TOPIC));
    }

    @Test
    void testProducerAndConsumer() throws Exception {
        producer.send(new ProducerRecord<>(TOPIC, "key1", "value1")).get();
        producer.flush();

        ConsumerRecords<String, String> records = ConsumerRecords.empty();
        int attempts = 0;
        while (records.isEmpty() && attempts < 5) {
            records = consumer.poll(Duration.ofSeconds(1));
            attempts++;
        }

        assertFalse(records.isEmpty(), "No messages received from kafka");

        var record = records.iterator().next();
        assertEquals("key1", record.key());
        assertEquals("value1", record.value());
    }
}
