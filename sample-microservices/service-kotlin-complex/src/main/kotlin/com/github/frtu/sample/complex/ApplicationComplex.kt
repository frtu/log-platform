package com.github.frtu.sample.complex

import com.github.frtu.sample.complex.persistence.basic.EmailEntity
import com.github.frtu.sample.complex.persistence.basic.IEmailRepository
import com.github.frtu.sample.complex.persistence.basic.STATUS
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
class ApplicationComplex {
    @Bean
    fun initializer(coroutineRepository: IEmailRepository): CommandLineRunner = CommandLineRunner {
        logger.debug("======================================")
        runBlocking {
            val entity = EmailEntity(
                "rndfred@gmail.com", "Mail subject",
                "Lorem ipsum dolor sit amet.", STATUS.SENT
            )
            coroutineRepository.save(entity)

            val list = mutableListOf<EmailEntity>()
            coroutineRepository.findAll().toList(list)
            logger.debug(list.toString())

//            val emailEntity = coroutineRepository.findById(entity.id!!)
//            logger.debug(emailEntity.toString())
        }
    }

    @Bean
    fun databaseInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(CompositeDatabasePopulator().apply {
                addPopulators(ResourceDatabasePopulator(ClassPathResource("db/migration/V0_1_0__h2-table-email.sql")))
            })
        }

    internal val logger = LoggerFactory.getLogger(this::class.java)
}

fun main(args: Array<String>) {
    runApplication<ApplicationComplex>(*args)
}