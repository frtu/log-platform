package com.github.frtu.sample.basic.config

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig {
    @Bean
    fun <K, V> receiverOptions(
        // Configuration coming from auto configuration prefix "spring.kafka"
        properties: KafkaProperties
    ) = ReceiverOptions.create<K, V>(properties.buildConsumerProperties())

    @Bean
    fun <K, V> senderOptions(
        // Configuration coming from auto configuration prefix "spring.kafka"
        properties: KafkaProperties
    ) = SenderOptions.create<K, V>(properties.buildProducerProperties())
}
