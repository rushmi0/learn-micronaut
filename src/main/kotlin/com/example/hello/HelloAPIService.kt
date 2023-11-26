package com.example.hello

import io.micronaut.context.annotation.Primary
import jakarta.inject.Singleton

@Primary
@Singleton
class HelloAPIService : MyServiceImp  {

    override fun helloAPIFromService(): String {
        return "Hello API from Service"
    }

}