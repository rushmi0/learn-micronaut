package com.example.basic;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

@MicronautTest
class HelloAPIControllerTest {

    @Inject
    @Client("/")
    lateinit var client: HttpClient; // ประกาศตัวแปร client เป็น Micronaut HTTP client และถูกเตรียมพร้อมให้ในภายหลัง.

    @Test
    fun helloAPIEndpointRespondsWithProperContent() {
        // ทดสอบว่า `/api/hello` endpoint ส่งคืนข้อความ "Hello API from Service"
        val response: String = client.toBlocking().retrieve("api/hello");
        assertEquals("Hello API from Service", response);
    }

    @Test
    fun helloAPIEndpointRespondsWithProperStatusCodeContent() {
        // ทดสอบว่า `/api/hello` endpoint ส่งคืนข้อความ "Hello API from Service" พร้อมกับ HTTP status code 200
        val response: HttpResponse<String> = client.toBlocking().exchange("api/hello", String::class.java);
        assertEquals("Hello API from Service", response.body());
        assertEquals(HttpStatus.OK, response.status);
    }

    @Test
    fun helloAPIControllerTest() {
        // ทดสอบว่า `/hello` endpoint ส่งคืนข้อความ "Hello API from Service" พร้อมกับ HTTP status code 200
        val response: HttpResponse<String> = client.toBlocking().exchange("hello", String::class.java);
        assertEquals("Hello API from Service", response.body());
        assertEquals(HttpStatus.OK, response.status);
    }

    // * Configuration Injection
    @Test
    fun helloAPIConfogEndpointRespondsMessageFromConfigFile() {
        // ทดสอบว่า `/hello/config` endpoint ส่งคืนข้อความที่กำหนดไว้ใน application.properties
        val response: HttpResponse<String> = client.toBlocking().exchange("hello/config", String::class.java);
        assertEquals("Hello API from application.properties", response.body());
        assertEquals(HttpStatus.OK, response.status);
    }

    // * Immutable Configuration Injection
    @Test
    fun helloAPITransfromationEndpointRespondsContentFromConfigFile() {
        // ทดสอบว่า `/hello/transformation` endpoint ส่งคืนข้อความที่แปลงเป็น JSON จากไฟล์คอนฟิก
        val response: HttpResponse<JsonNode> = client.toBlocking().exchange("hello/transformation", JsonNode::class.java);
        assertEquals("{de=Hello API, en=Hello World}", response.body().value.toString());
        assertEquals("Hello API", response.body().get("de").value.toString());
        assertEquals("Hello World", response.body().get("en").value.toString());
        assertEquals(HttpStatus.OK, response.status);
    }

}
