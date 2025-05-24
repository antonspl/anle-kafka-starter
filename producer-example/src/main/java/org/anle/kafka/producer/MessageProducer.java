package org.anle.kafka.producer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    @Value("${anle.kafka.producer.topic}")
    private String topic;

    private final Producer<String, String> producer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public MessageProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    @PostConstruct
    public void start() {
        log.info("Sending messages to topic: {}", topic);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                String key = "RandomNumber";
                String value = "Number is " + ThreadLocalRandom.current().nextInt(1, 100);
                producer.send(new ProducerRecord<>(topic, key, value));
                log.info("Sent key: {}, value: {}", key, value);
            } catch (Exception e) {
                log.error("Error sending message to Kafka", e);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
        producer.close();
    }
}
