package com.github.frtu.sample.basic.rpc.web

import com.github.frtu.logs.core.RpcLogger
import com.github.frtu.logs.core.RpcLogger.requestBody
import com.github.frtu.logs.core.RpcLogger.responseBody
import com.github.frtu.logs.core.RpcLogger.uri
import com.github.frtu.logs.core.StructuredLogger.message
import com.github.frtu.sample.basic.domain.SubscriptionProcessor
import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
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
class Router(
    private val processor: SubscriptionProcessor,
) {
    internal val logger = LoggerFactory.getLogger(this::class.java)
    internal val rpcLogger = RpcLogger.create(logger)

    @Bean
    fun route(): RouterFunction<*> = coRouter {
        val uriPath = "/v1/subscriptions"
        GET(uriPath) {
            rpcLogger.info(uri(uriPath), message("find all"))
            ok().json().bodyAndAwait(processor.queryMany())
        }
        GET("/v1/subscriptions/{id}") { serverRequest ->
            val id = serverRequest.pathVariable("id")
            val entity = processor.queryOne(id.toLong())
            when {
                entity != null -> ok().json().bodyValueAndAwait(entity)
                else -> notFound().buildAndAwait()
            }
        }
        POST(uriPath) { serverRequest ->
            val accountSubscriptionEntity = serverRequest.awaitBody<AccountSubscriptionEntity>()
            val createdId = processor.insert(accountSubscriptionEntity)
            rpcLogger.info(uri(uriPath), requestBody(accountSubscriptionEntity), responseBody(createdId))
            created(URI.create("$uriPath/$createdId")).buildAndAwait()
        }
    }
}
