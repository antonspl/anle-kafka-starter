package org.anle.kafka.starter.ifc;

public interface MessageProducerWrapper<T> {
    void send(String topic, String key, T value);
    void close();
}
