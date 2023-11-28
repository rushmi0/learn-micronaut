package com.example.hello

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.serde.annotation.Serdeable
import javax.validation.constraints.NotBlank


@Serdeable.Serializable
@ConfigurationProperties("hello.api.transformation")
interface HelloAPItransformationConfig {

    // * Validation

    @NotBlank
    fun getDe(): String

    @NotBlank
    fun getEn(): String

}