package com.microservices.chapter04.exception

import java.lang.RuntimeException

class CustomerExistException(override val message: String): RuntimeException()