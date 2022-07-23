package publisher

import com.github.frtu.sample.basic.config.KafkaConfig
import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
import com.github.frtu.sample.basic.persistence.basic.STATUS
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(KafkaConfig::class)
class PublisherTestApplication {
    @Bean
    fun initializer(eventSink: EventSink): CommandLineRunner = CommandLineRunner {
        logger.debug("======================================")
        runBlocking {
            val entity = AccountSubscriptionEntity(
                "Fred TU", "rndfred@gmail.com",
                "+33123456789", STATUS.ACTIVE
            )
            eventSink.emit(entity)
            logger.debug("Entity sent:{}", entity)
        }
    }

    internal val logger = LoggerFactory.getLogger(this::class.java)
}

fun main(args: Array<String>) {
    System.getProperties()["server.port"] = 8083
    runApplication<PublisherTestApplication>(*args)
}