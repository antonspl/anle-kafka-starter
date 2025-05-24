package org.anle.kafka.producer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.anle.kafka.starter.ifc.MessageProducerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "anle.kafka.producer.value-serializer", havingValue = "org.apache.kafka.common.serialization.StringSerializer")
public class StringMessageProducerExample {

    private static final Logger log = LoggerFactory.getLogger(StringMessageProducerExample.class);

    @Value("${anle.kafka.producer.topic}")
    private String topic;

    private final MessageProducerWrapper<String> producer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public StringMessageProducerExample(MessageProducerWrapper<String> producer) {
        this.producer = producer;
    }

    @PostConstruct
    public void start() {
        log.info("Sending messages to topic: {}", topic);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                String key = "RandomNumber";
                String value = "Number is " + ThreadLocalRandom.current().nextInt(1, 100);
                producer.send(topic, key, value);
                log.info("Sent key: {}, value: {}", key, value);
            } catch (Exception e) {
                log.error("Error sending message to Kafka", e);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
        producer.close();
    }
}
