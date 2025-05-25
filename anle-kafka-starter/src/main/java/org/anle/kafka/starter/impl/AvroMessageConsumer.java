package org.anle.kafka.starter.impl;

import org.anle.kafka.avro.NumberCreated;
import org.anle.kafka.starter.ifc.MessageConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collection;

public class AvroMessageConsumer implements MessageConsumer<NumberCreated> {

    private final Consumer<String, NumberCreated> consumer;

    public AvroMessageConsumer(Consumer<String, NumberCreated> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void subscribe(Collection<String> topics) {
        consumer.subscribe(topics);
    }

    @Override
    public ConsumerRecords<String, NumberCreated> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    @Override
    public void close() {
        consumer.close();
    }
}
