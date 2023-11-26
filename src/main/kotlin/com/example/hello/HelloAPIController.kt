package com.example.hello

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject

// * Dependency Injection - Interface

@Controller("hello")
class HelloAPIController @Inject constructor(
    private val service: MyServiceImp // MyServiceImp : มาจาก Interface
) {

    @Get(produces = [MediaType.TEXT_PLAIN])
    fun helloAPI(): String {
        return service.helloAPIFromService()
    }
}