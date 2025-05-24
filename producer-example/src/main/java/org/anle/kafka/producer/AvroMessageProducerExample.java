package org.anle.kafka.producer;

import jakarta.annotation.PreDestroy;
import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.ifc.MessageProducerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "anle.kafka.producer.value-serializer", havingValue = "io.confluent.kafka.serializers.KafkaAvroSerializer")
public class AvroMessageProducerExample {

    private static final Logger log = LoggerFactory.getLogger(AvroMessageProducerExample.class);
    private static final String NUMBER_CREATED = "NUMBER_CREATED_KEY";

    private final MessageProducerWrapper<NumberCreated> producer;
    private final String topic;

    public AvroMessageProducerExample(
            MessageProducerWrapper<NumberCreated> producer,
            @Value("${anle.kafka.producer.topic}") String topic
    ) {
        this.producer = producer;
        this.topic = "avro-"+topic;
    }

    @Scheduled(fixedRate = 2000)
    public void produceMessages() {
        var event = new NumberCreated(UUID.randomUUID().toString().substring(0, 5),
                String.valueOf(ThreadLocalRandom.current().nextInt(1, 100)));
        producer.send(topic, NUMBER_CREATED, event);
        log.info("Sent key: {}, value: {}", NUMBER_CREATED, event);
    }

    @PreDestroy
    public void stop() {
        producer.close();
    }
}
