package com.github.frtu.sample.basic.source

import com.github.frtu.sample.basic.domain.SubscriptionProcessor
import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener

@Configuration
class EventSourceSync(
    val subscriptionProcessor: SubscriptionProcessor,
) {
    @KafkaListener(topics = ["app-subscriptions-sync"], groupId = "group")
    fun listenReports(accountSubscriptionEntity: AccountSubscriptionEntity) {
        logger.info("Got report with: {}", accountSubscriptionEntity)
        runBlocking {
            subscriptionProcessor.insert(accountSubscriptionEntity)
        }
        logger.info("Saved : $accountSubscriptionEntity")
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
}