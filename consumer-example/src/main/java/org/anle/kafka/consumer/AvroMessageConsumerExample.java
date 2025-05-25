package org.anle.kafka.consumer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.ifc.MessageConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
@ConditionalOnProperty(name = "anle.kafka.consumer.value-deserializer", havingValue = "io.confluent.kafka.serializers.KafkaAvroDeserializer")
public class AvroMessageConsumerExample {

    private static final Logger log = LoggerFactory.getLogger(AvroMessageConsumerExample.class);

    private final MessageConsumer<NumberCreated> consumer;
    private final String topic;

    public AvroMessageConsumerExample(
            MessageConsumer<NumberCreated> consumer,
            @Value("${anle.kafka.consumer.topic}") String topic
    ) {
        this.consumer = consumer;
        this.topic = "avro-"+topic;
    }

    @PostConstruct
    public void subscribe() {
        consumer.subscribe(Collections.singletonList(topic));
        log.info("Subscribed to topic: {}", topic);
    }

    @Scheduled(fixedRate = 1000)
    public void pollMessages() {
        try {
            ConsumerRecords<String, NumberCreated> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, NumberCreated> record : records) {
                log.info("Consumed message: key={}, value={}, offset={}", record.key(), record.value(), record.offset());
            }
        } catch (Exception e) {
            log.error("Error polling messages from Kafka", e);
        }
    }

    @PreDestroy
    public void stop() {
        consumer.close();
    }
}
