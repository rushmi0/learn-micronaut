package com.example

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable

@Controller("/user")
class UserController {

    @Introspected
    @Serdeable.Serializable
    data class NewAccount(val userName: String, val authKey: String)

    @Post("/sign-up")
    fun signUpNewAccount(
        @Header("AccountType") accountType: String?,
        @Body payload: NewAccount
    ): HttpResponse<String> {

        val userName = payload.userName
        val publicKey = payload.authKey

        // ทำสิ่งที่คุณต้องการทำกับข้อมูลที่ได้รับ

        return HttpResponse.ok("User signed up successfully")
    }
}
