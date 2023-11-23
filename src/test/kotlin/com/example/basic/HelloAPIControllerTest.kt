package com.example.basic

import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
class HelloAPIControllerTest {

    @Inject
    @Client("/")
    lateinit var client: HttpClient // ประกาศตัวแปร client เป็น Micronaut HTTP client และถูกเตรียมพร้อมให้ในภายหลัง.

    @Test
    fun helloAPIEndpointRespondsWithProperContent() {
        val response = client.toBlocking().retrieve("api/hello")
        assertEquals("Hello API", response)
    }

    @Test
    fun helloAPIEndpointRespondsWithProperStatusCodeContent() {
        val response = client.toBlocking().exchange("api/hello", String::class.java)
        assertEquals("Hello API", response.body())
        assertEquals(HttpStatus.OK, response.status)
    }


}
