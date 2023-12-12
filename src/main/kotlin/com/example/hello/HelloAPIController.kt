package com.example.hello

import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.serde.annotation.Serdeable
import org.slf4j.LoggerFactory

@Controller("hello")
class HelloAPIController(
    private val service: MyServiceImp,
    @Value("\${hello.world.message}") private val helloAPIConfig: String,
    private val dataTransformationConfig: HelloAPItransformationConfig
) {

    private val LOG = LoggerFactory.getLogger(HelloAPIController::class.java)

    @Introspected
    @Serdeable.Deserializable
    data class DataForm(
        val name: String,
        val age: Int
    )

    @Put(
        uri = "/{id}",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun updateData(
        id: Long,
        @Body dataForm: DataForm
    ): HttpResponse<String> {
        println(dataForm.name)
        return HttpResponse.ok("Data updated successfully")
    }


    @Get(produces = [MediaType.TEXT_PLAIN])
    fun helloAPI(): String {
        LOG.debug("Called the hello API!")
        return service.helloAPIFromService()
    }

    // * Configuration Injection
    @Get(uri = "/config", produces = [MediaType.TEXT_PLAIN])
    fun helloConfig(): String {
        LOG.debug("Return Hello API from Config Message: $helloAPIConfig")
        return helloAPIConfig
    }

    // * Immutable Configuration Injection
    @Get(uri = "/transformation", produces = [MediaType.APPLICATION_JSON])
    fun helloAPITransfromation(): HelloAPItransformationConfig {
        return dataTransformationConfig
    }


}