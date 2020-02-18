package com.microservices.chapter05.service

import com.microservices.chapter05.entity.Customer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomerService {
    fun getCustomer(id: Int): Mono<Customer>

    fun searchCustomers(nameFilter: String): Flux<Customer>

    fun createCustomer(customerMono: Mono<Customer>): Mono<Customer>

    fun deleteCustomer(id: Int): Mono<Boolean>
}