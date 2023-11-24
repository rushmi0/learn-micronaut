package com.example.basic

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject

@Controller("api/hello")
class HelloAPI {


    @Inject
    lateinit var service: HelloAPIService


    @Get(produces = [MediaType.TEXT_PLAIN])
    fun helloAPI(): String {
        return service.helloAPIFromService()
    }

}