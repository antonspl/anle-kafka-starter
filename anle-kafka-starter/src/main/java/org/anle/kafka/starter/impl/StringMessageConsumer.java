package org.anle.kafka.starter.impl;

import org.anle.kafka.starter.ifc.MessageConsumerWrapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collection;

public class StringMessageConsumer implements MessageConsumerWrapper<String> {

    private final Consumer<String, String> consumer;

    public StringMessageConsumer(Consumer<String, String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void subscribe(Collection<String> topics) {
        consumer.subscribe(topics);
    }

    @Override
    public ConsumerRecords<String, String> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    @Override
    public void close() {
        consumer.close();
    }
}
