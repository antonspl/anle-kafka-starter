package org.anle.kafka.starter.impl;

import org.anle.kafka.starter.ifc.MessageProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class StringMessageProducer implements MessageProducer<String> {

    private final KafkaProducer<String, String> producer;

    public StringMessageProducer(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void send(String topic, String key, String value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    @Override
    public void close() {
        producer.close();
    }
}
