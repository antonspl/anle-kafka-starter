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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class KafkaMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public KafkaMessageConsumer(KafkaConsumer<String, String> consumer, @Value("${anle.kafka.consumer.topic}") String topic) {
        this.consumer = consumer;
        this.topic = topic;
    }

    @PostConstruct
    public void start() {
        consumer.subscribe(Collections.singletonList(topic));
        executor.submit(() -> {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    log.warn("Consumed message: key=%s, value=%s, offset=%d".formatted(record.key(), record.value(), record.offset()));
                }
            }
        });
    }

    @PreDestroy
    public void stop() {
        running = false;
        executor.shutdownNow();
        consumer.close();
    }
}
