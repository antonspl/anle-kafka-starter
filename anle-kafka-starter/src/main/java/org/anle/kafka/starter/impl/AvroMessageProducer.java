package org.anle.kafka.starter.impl;

import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.ifc.MessageProducerWrapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class AvroMessageProducer implements MessageProducerWrapper<NumberCreated> {

    private final KafkaProducer<String, NumberCreated> producer;

    public AvroMessageProducer(KafkaProducer<String, NumberCreated> producer) {
        this.producer = producer;
    }

    @Override
    public void send(String topic, String key, NumberCreated value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    @Override
    public void close() {
        producer.close();
    }
}
