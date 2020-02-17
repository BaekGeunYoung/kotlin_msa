package com.microservices.chapter04.entity

data class Customer(
        var id: Int = 0,
        var name: String = "",
        var telephone: Telephone? = null
) {
    data class Telephone(
            var countryCode: String = "",
            var telephoneNumber: String = ""
    )
}