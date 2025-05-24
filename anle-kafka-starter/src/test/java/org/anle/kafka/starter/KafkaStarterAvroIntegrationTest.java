package org.anle.kafka.starter;

import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.config.AvroConsumerConfig;
import org.anle.kafka.starter.config.AvroProducerConfig;
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
                AvroProducerConfig.class,
                AvroConsumerConfig.class
        },
        properties = {
                "anle.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "anle.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "anle.kafka.producer.value-serializer=io.confluent.kafka.serializers.KafkaAvroSerializer",
                "anle.kafka.producer.schema-registry-url=http://localhost:8081",

                "anle.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "anle.kafka.consumer.group-id=test-group",
                "anle.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                "anle.kafka.consumer.value-deserializer=io.confluent.kafka.serializers.KafkaAvroDeserializer",
                "anle.kafka.consumer.enable-auto-commit=true",
                "anle.kafka.consumer.offset-reset=earliest",
                "anle.kafka.consumer.schema-registry-url=http://localhost:8081"
        }
)
@EmbeddedKafka(partitions = 1, topics = {"avro-test-topic"}, brokerProperties = {"replication.factor=1", "min.insync.replicas=1"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaStarterAvroIntegrationTest {

    private static final String TOPIC = "avro-test-topic";

    @Autowired
    private KafkaProducer<String, NumberCreated> producer;

    @Autowired
    private KafkaConsumer<String, NumberCreated> consumer;

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
        producer.send(new ProducerRecord<>(TOPIC, "key",new NumberCreated("id", "value"))).get();
        producer.flush();

        ConsumerRecords<String, NumberCreated> records = ConsumerRecords.empty();
        int attempts = 0;
        while (records.isEmpty() && attempts < 5) {
            records = consumer.poll(Duration.ofSeconds(1));
            attempts++;
        }

        assertFalse(records.isEmpty(), "No messages received from kafka");

        var record = records.iterator().next();
        assertEquals("key", record.key());
        assertEquals(new NumberCreated("id", "value"), record.value());
    }
}
