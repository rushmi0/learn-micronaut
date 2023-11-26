package com.example.hello

import jakarta.inject.Singleton

@Singleton
class SecondHelloAPIService : MyServiceImp  {

    override fun helloAPIFromService(): String {
        return "Hello API from Second Service"
    }

}