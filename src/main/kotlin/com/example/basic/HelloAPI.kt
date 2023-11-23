package com.example.basic

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("api/hello")
class HelloAPI {

    @Get(produces = [MediaType.TEXT_PLAIN])
    fun helloAPI(): String {
        return "Hello API"
    }

}