package com.github.frtu.sample.basic.source

import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionRepository
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.ReceiverOptions

@Configuration
class EventSource(
    @Value(value = "\${application.broker.topics.subscriptions}")
    val topic: String,
    receiverOptions: ReceiverOptions<String, AccountSubscriptionEntity>,
) {
    val reactiveKafkaConsumerTemplate = ReactiveKafkaConsumerTemplate(
        receiverOptions
            .withKeyDeserializer(StringDeserializer())
            .withValueDeserializer(
                JsonDeserializer<AccountSubscriptionEntity>().apply {
                    this.addTrustedPackages("*")
                }
            )
            .addAssignListener { assignments -> logger.info("Adding assignments:{}", assignments) }
            .subscription(listOf(topic))
    )

    @Bean
    fun subscriptionSource(coroutineRepository: AccountSubscriptionRepository): ApplicationRunner = ApplicationRunner {
        logger.debug("======================================")
        reactiveKafkaConsumerTemplate
            .receiveAutoAck() // .delayElements(Duration.ofSeconds(2L)) // BACKPRESSURE
            .doOnNext { consumerRecord ->
                logger.info(
                    "received key={}, value={} from topic={}, offset={}",
                    consumerRecord.key(),
                    consumerRecord.value(),
                    consumerRecord.topic(),
                    consumerRecord.offset()
                )
            }
            .map { it.value() }
            .doOnNext { fakeConsumerDTO: Any? ->
                logger.info(
                    "successfully consumed {}={}",
                    AccountSubscriptionEntity::class.java.simpleName, fakeConsumerDTO
                )
            }
            .doOnError { throwable: Throwable ->
                logger.error("something bad happened while consuming : {}", throwable.message)
            }
            .subscribe()
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
}