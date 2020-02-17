package com.microservices.chapter04.controller

import com.microservices.chapter04.handler.CustomerHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.router

@Component
class CustomerRouter(
        @Autowired private val customerHandler: CustomerHandler
) {
    @Bean
    fun customerRoutes(): RouterFunction<*> = router {
        "/functional".nest {
            "/customer".nest {
                GET("/{id}", customerHandler::get)
                POST("/", customerHandler::create)
            }
            "customers".nest {
                GET("/", customerHandler::search)
            }
        }
    }
}