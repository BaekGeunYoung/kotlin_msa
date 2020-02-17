package com.microservices.chapter04.service

import com.microservices.chapter04.entity.Customer
import com.microservices.chapter04.exception.CustomerExistException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.util.concurrent.ConcurrentHashMap

@Component
class CustomerServiceImpl: CustomerService {
    companion object {
        val initialCustomers = arrayOf(
                Customer(1, "name1"),
                Customer(2, "name2"),
                Customer(3, "name3", Customer.Telephone("+44", "0101010101"))
        )
    }

    val customers = ConcurrentHashMap<Int, Customer>(initialCustomers.associateBy(Customer::id))

    override fun getCustomer(id: Int): Mono<Customer> = customers[id]?.toMono() ?: Mono.empty()

    override fun searchCustomers(nameFilter: String): Flux<Customer> = customers.filter {
        it.value.name.contains(nameFilter, true)
    }.map ( Map.Entry<Int, Customer>::value ).toFlux()

    override fun createCustomer(customerMono: Mono<Customer>): Mono<Customer> {
        return customerMono.flatMap {
            if (customers[it.id] == null) {
                customers[it.id] = it
                it.toMono()
            } else {
                Mono.error(CustomerExistException("Customer ${it.id} already exist"))
            }
        }
    }
}