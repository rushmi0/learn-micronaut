package com.example.hello

import jakarta.inject.Singleton

@Singleton
class HelloAPIService : MyServiceImp  {

    override fun helloAPIFromService(): String {
        return "Hello API from Service"
    }

}