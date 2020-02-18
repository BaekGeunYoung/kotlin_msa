package com.microservices.chapter05.controller

import com.microservices.chapter05.handler.CustomerHandler
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
                DELETE("/{id}", customerHandler::delete)
            }
            "customers".nest {
                GET("/", customerHandler::search)
            }
        }
    }
}