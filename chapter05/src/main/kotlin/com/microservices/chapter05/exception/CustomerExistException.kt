package com.microservices.chapter05.exception

import java.lang.RuntimeException

class CustomerExistException(override val message: String): RuntimeException()