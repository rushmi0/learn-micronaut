package com.example

import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.json.tree.JsonNode
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals

@MicronautTest
class LearnkotlinmicronautTest {

    @Inject
    @Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var application: EmbeddedApplication<*>

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application.isRunning)
    }


    @Test
    fun requestQueryParametersTest() {
        val response: HttpResponse<JsonNode> = client.toBlocking().exchange("api/filter?type=Fruit&name=Apple", JsonNode::class.java);
        println(response.body().value.toString())
        assertEquals("[{type=Fruit, name=Apple, price=1.5}]", response.body().value.toString());
    }

}
