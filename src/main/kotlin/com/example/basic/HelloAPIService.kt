package com.example.basic

import jakarta.inject.Singleton

@Singleton
class HelloAPIService {

    fun helloAPIFromService(): String {
        return "Hello API from Service"
    }

}