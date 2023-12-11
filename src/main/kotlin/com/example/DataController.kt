package com.example

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.serde.annotation.Serdeable

@Controller("/api/data")
class DataController {

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    fun getJSON(@Body data: Data): HttpResponse<String> {
        // ทำอะไรกับข้อมูลที่ได้รับเช่น บันทึกลงฐานข้อมูล หรือประมวลผลต่อไป
        println("Received data: $data")

        // ส่งคำตอบกลับ
        return HttpResponse.ok("Data received successfully")
    }
}

@Serdeable.Serializable
data class Data(val value: Int, val item: String)
