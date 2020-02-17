package com.microservices.chapter04.handler

import com.microservices.chapter04.entity.Customer
import com.microservices.chapter04.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.onErrorResume

@Component
class CustomerHandler(
        @Autowired private val customerService: CustomerService
) {
    fun get(serverRequest: ServerRequest) =
            customerService.getCustomer(serverRequest.pathVariable("id").toInt())
                    .flatMap { ok().body(fromObject(it)) }
                    .switchIfEmpty(notFound().build())

    fun search(serverRequest: ServerRequest) =
            ok().body(customerService.searchCustomers(serverRequest.queryParam("nameFilter").orElse("")), Customer::class.java)


    fun create(serverRequest: ServerRequest) =
            customerService.createCustomer(serverRequest.bodyToMono()).flatMap {
                status(HttpStatus.CREATED).body(fromObject(it))
            }.onErrorResume(Exception::class) {
                badRequest().body(fromObject("error"))
            }
}