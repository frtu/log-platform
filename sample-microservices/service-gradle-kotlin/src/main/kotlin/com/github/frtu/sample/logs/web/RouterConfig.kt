package com.github.frtu.sample.logs.web

import com.github.frtu.logs.core.RpcLogger
import com.github.frtu.logs.core.RpcLogger.requestBody
import com.github.frtu.logs.core.RpcLogger.responseBody
import com.github.frtu.logs.core.RpcLogger.uri
import kotlinx.coroutines.reactive.asFlow
import java.net.URI
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Flux

@Configuration
class RouterConfig {
    internal val logger = LoggerFactory.getLogger(this::class.java)
    internal val rpcLogger = RpcLogger.create(logger)

    @Bean
    fun route(): RouterFunction<*> = coRouter {
        val uriPath = "/v1/resources"
        GET("$uriPath/{id}") { serverRequest ->
            val id = serverRequest.pathVariable("id")
            println(id)
            ServerResponse.ok()
                .bodyAndAwait(Flux.just("ok").asFlow())
        }
        POST(uriPath) { serverRequest ->
            val body = serverRequest.awaitBody<String>()
            rpcLogger.info(uri(uriPath), requestBody(body))
            // Create and get ID
            val createdId = UUID.randomUUID()
            rpcLogger.info(uri(uriPath), responseBody(createdId))
            created(URI.create("$uriPath/$createdId")).buildAndAwait()
        }
    }
}
