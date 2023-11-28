package com.example.hello

import io.micronaut.context.annotation.Value
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory

@Controller("hello")
class HelloAPIController(
    private val service: MyServiceImp,
    @Value("\${hello.world.message}") private val helloAPIConfig: String,
    private val dataTransformationConfig: HelloAPItransformationConfig
) {

    private val LOG = LoggerFactory.getLogger(HelloAPIController::class.java)

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
    fun helloAPITransfromation(): HelloAPItransformationConfig  {
        return dataTransformationConfig
    }


}