plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.20"
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.0"
    id("io.micronaut.aot") version "4.2.0"
}

version = "0.1"
group = "com.example"

val kotlinVersion = project.properties["kotlinVersion"]
repositories {
    mavenCentral()
}

dependencies {


    // https://mvnrepository.com/artifact/io.micronaut/micronaut-websocket
    implementation("io.micronaut:micronaut-websocket:4.2.0")


    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("org.apache.logging.log4j:log4j-api")
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.20.0"))

    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("io.micronaut:micronaut-http-client")

    runtimeOnly("org.apache.logging.log4j:log4j-core")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl")

    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    runtimeOnly("org.postgresql:postgresql")

    // https://mvnrepository.com/artifact/org.bouncycastle/bcpkix-jdk15on
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    // https://mvnrepository.com/artifact/io.micronaut.sql/micronaut-jdbc-hikari
    testImplementation("io.micronaut.sql:micronaut-jdbc-hikari:3.0.1")


}


tasks {

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

}

kotlin {

    sourceSets.all {
        languageSettings {
            version = 2.0
        }
    }
}


application {
    mainClass.set("com.example.ApplicationKt")
}
//java {
//    sourceCompatibility = JavaVersion.toVersion("17")
//}


graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}



