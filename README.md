## Kafka Spring Boot Starter
Spring Boot Starter для интеграции с Apache Kafka, включающий автонастройку, поддержку Avro и готовые примеры использования.

## Возможности
- Автоматическое создание Kafka-продюсера и консьюмера
- Гибкая настройка через application.properties
- Безопасное завершение работы с Kafka
- Поддержка формата сообщений Avro
- Описание схем через Avro IDL
- Примеры использования в виде отдельных приложений (продюсер и консьюмер)
- Автокомплит проперти в IDE
- Юнит-тесты конфигурации и интеграционный тест с Embedded Kafka (для Avro и String потоков)

## Структура проекта
    kafka-starter/
    ├── kafka-spring-boot-starter/   # Сам стартер
    ├── producer-example/            # Пример использования продюсера
    ├── consumer-example/            # Пример использования консьюмера
    └── avro-idl/                    # Avro IDL схемы

## Как собрать и запустить
Требования: JDK 17, Maven, Docker

```bash
# Сборка всех модулей
mvn clean install
```
```bash
# Запуск Kafka и UI через
docker-compose up --build -d
```
```bash
# Запуск продюсера
java -jar producer-example/target/producer-example-1.0-SNAPSHOT.jar
```
```bash
# Запуск консьюмера
java -jar consumer-example/target/consumer-example-1.0-SNAPSHOT.jar
```
После запуска docker-compose будут подняты:

- Kafka-брокер
- UI Kafka по адресу: http://localhost:9000
- Два топика: один для строковых сообщений, второй для Avro

## Поведение примеров:

- Producer: по расписанию раз в 2 секунды отправляет случайное число в диапазоне1..100 в Kafka

- Consumer: считывает сообщения и выводит в лог, с автоматическим коммитом оффсетов. Начнёт читать с последнего непрочитанного после перезапуска

## Настройка
Все необходимые свойства указываются в application.properties. Контекст приложения не запустится, если какие-либо обязательные свойства отсутствуют

## Примеры:

В зависимости от значения value-deserializer создаётся один из бинов:

org.apache.kafka.common.serialization.StringDeserializer - StringConsumer

io.confluent.kafka.serializers.KafkaAvroDeserializer - AvroConsumer

Аналогично - для продюсера по value-serializer
