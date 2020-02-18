package com.microservices.chapter05.service

import com.microservices.chapter05.entity.Customer
import com.microservices.chapter05.exception.CustomerExistException
import com.microservices.chapter05.repository.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.util.concurrent.ConcurrentHashMap

@Component
class CustomerServiceImpl(
        @Autowired private val customerRepository: CustomerRepository
): CustomerService {
    override fun getCustomer(id: Int) = customerRepository.findById(id)

    override fun searchCustomers(nameFilter: String) = customerRepository.findCustomer(nameFilter)

    override fun createCustomer(customerMono: Mono<Customer>) = customerRepository.create(customerMono)

    override fun deleteCustomer(id: Int) = customerRepository.deleteById(id).map { it.deletedCount > 0 }
}