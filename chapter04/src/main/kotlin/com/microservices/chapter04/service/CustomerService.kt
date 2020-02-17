package com.microservices.chapter04.service

import com.microservices.chapter04.entity.Customer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomerService {
    fun getCustomer(id: Int): Mono<Customer>
    fun searchCustomers(nameFilter: String): Flux<Customer>
    fun createCustomer(customerMono: Mono<Customer>): Mono<Customer>
}