package com.github.frtu.sample.complex.web

import com.github.frtu.logs.core.RpcLogger
import com.github.frtu.logs.core.RpcLogger.requestBody
import com.github.frtu.logs.core.RpcLogger.responseBody
import com.github.frtu.logs.core.RpcLogger.uri
import com.github.frtu.logs.core.StructuredLogger.message
import com.github.frtu.sample.complex.persistence.basic.EmailEntity
import com.github.frtu.sample.complex.persistence.basic.IEmailRepository
import java.net.URI
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

@Configuration
class RouterConfig(
    private val repository: IEmailRepository,
) {
    internal val logger = LoggerFactory.getLogger(this::class.java)
    internal val rpcLogger = RpcLogger.create(logger)

    @Bean
    fun route(): RouterFunction<*> = coRouter {
        val uriPath = "/v1/emails"
        GET(uriPath) {
            rpcLogger.info(uri(uriPath), message("find all"))
            ok().json().bodyAndAwait(repository.findAll())
        }
        GET("/v1/emails/{id}") { serverRequest ->
            val id = serverRequest.pathVariable("id")
            val entity = repository.findById(id.toLong())
            when {
                entity != null -> ok().json().bodyValueAndAwait(entity)
                else -> notFound().buildAndAwait()
            }
        }
        POST(uriPath) { serverRequest ->
            val emailEntity = serverRequest.awaitBody<EmailEntity>()
            val createdId = repository.save(emailEntity)
            rpcLogger.info(uri(uriPath), requestBody(emailEntity), responseBody(createdId))
            created(URI.create("$uriPath/$createdId")).buildAndAwait()
        }
    }
}
