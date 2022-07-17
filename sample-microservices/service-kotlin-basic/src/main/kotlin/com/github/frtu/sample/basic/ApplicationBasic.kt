package com.github.frtu.sample.basic

import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionRepository
import com.github.frtu.sample.basic.persistence.basic.STATUS
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
//@EnableR2dbcRepositories
class ApplicationBasic {
    @Bean
    fun initializer(coroutineRepository: AccountSubscriptionRepository): CommandLineRunner = CommandLineRunner {
        logger.debug("======================================")
        runBlocking {
            val entity = AccountSubscriptionEntity(
                "Fred TU", "rndfred@gmail.com",
                "+33123456789", STATUS.ACTIVE
            )
            coroutineRepository.save(entity)

            val list = mutableListOf<AccountSubscriptionEntity>()
            coroutineRepository.findAll().toList(list)
            logger.debug(list.toString())
        }
    }

    @Bean
    fun databaseInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(CompositeDatabasePopulator().apply {
                addPopulators(ResourceDatabasePopulator(ClassPathResource("db/migration/V0_1_0__h2-table-account_subscription.sql")))
            })
        }

    internal val logger = LoggerFactory.getLogger(this::class.java)
}

fun main(args: Array<String>) {
    runApplication<ApplicationBasic>(*args)
}