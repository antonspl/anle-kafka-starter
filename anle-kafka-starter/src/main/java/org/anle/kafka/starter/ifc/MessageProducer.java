package org.anle.kafka.starter.ifc;

public interface MessageProducer<T> {
    void send(String topic, String key, T value);
    void close();
}
