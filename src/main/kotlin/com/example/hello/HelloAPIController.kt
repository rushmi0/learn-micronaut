package com.example.hello

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// * Dependency Injection - Interface

@Controller("hello")
class HelloAPIController @Inject constructor(
    private val service: MyServiceImp // MyServiceImp : มาจาก Interface
) {

    private val LOG = LoggerFactory.getLogger(HelloAPIController::class.java)

    @Get(produces = [MediaType.TEXT_PLAIN])
    fun helloAPI(): String {
        LOG.debug("Called the hello API!")
        return service.helloAPIFromService()
    }
}
