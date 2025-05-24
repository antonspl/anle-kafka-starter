package producer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public MessageConsumer(KafkaConsumer<String, String> consumer,
                           @Value("${anle.kafka.consumer.topic}") String topic) {
        this.consumer = consumer;
        this.topic = topic;
    }

    @PostConstruct
    public void start() {
        consumer.subscribe(Collections.singletonList(topic));
        log.info("Subscribed to topic: {}", topic);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("Consumed message: key={}, value={}, offset={}", record.key(), record.value(), record.offset());
                }
            } catch (Exception e) {
                log.error("Error polling messages from Kafka", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
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
        consumer.close();
    }
}
