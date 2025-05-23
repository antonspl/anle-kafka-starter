package org.anle.kafka.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageSender implements CommandLineRunner {

    @Value("${anle.kafka.producer.topic}")
    private String topic;

    private final Producer<String, String> producer;

    public KafkaMessageSender(Producer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) {
        System.out.println("Sending messages to topic: " + topic);

        for (int i = 1; i <= 5; i++) {
            String key = "key-" + i;
            String value = "Test message " + i;
            producer.send(new ProducerRecord<>(topic, key, value));
            System.out.println("Sent: " + value);
        }

        producer.flush();
    }
}
