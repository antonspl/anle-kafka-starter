package org.anle.kafka.starter.ifc;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collection;

public interface MessageConsumer<T> {
    void subscribe(Collection<String> topics);
    ConsumerRecords<String, T> poll(Duration timeout);
    void close();
}
