package org.anle.kafka.producer;

import org.anle.kafka.starter.properties.KafkaProperties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageSender implements CommandLineRunner {
    private final Producer<String, String> producer;
    private final KafkaProperties kafkaProperties;

    public KafkaMessageSender(Producer<String, String> producer, KafkaProperties kafkaProperties) {
        this.producer = producer;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void run(String... args) {
        String topic = kafkaProperties.getTopic();
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
