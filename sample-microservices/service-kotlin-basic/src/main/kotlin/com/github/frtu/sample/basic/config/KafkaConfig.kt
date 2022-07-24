package com.github.frtu.sample.basic.config

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.kafkaclients.KafkaTelemetry
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig {
    @Bean
    fun kafkaTelemetry(openTelemetry: OpenTelemetry) = KafkaTelemetry.create(openTelemetry)

    @Bean
    fun <K, V> receiverOptions(
        // Configuration coming from auto configuration prefix "spring.kafka"
        properties: KafkaProperties,
        kafkaTelemetry: KafkaTelemetry,
    ): ReceiverOptions<K, V> {
        val consumerProperties = properties.buildConsumerProperties()
        consumerProperties.putAll(kafkaTelemetry.metricConfigProperties())
        return ReceiverOptions.create(consumerProperties)
    }

    @Bean
    fun <K, V> senderOptions(
        // Configuration coming from auto configuration prefix "spring.kafka"
        properties: KafkaProperties,
        kafkaTelemetry: KafkaTelemetry,
    ): SenderOptions<K, V> {
        val producerProperties = properties.buildProducerProperties()
        producerProperties.putAll(kafkaTelemetry.metricConfigProperties())
        return SenderOptions.create(producerProperties)
    }
}
