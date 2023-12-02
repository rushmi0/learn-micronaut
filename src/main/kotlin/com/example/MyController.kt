package com.example

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.serde.annotation.Serdeable
import java.util.Optional
import java.util.stream.Collectors

@Controller("/api")
class MyController {

    @Introspected
    data class MyRequestData(
        val name: String,
        val value: Optional<Int>
    )

    @Get("/query-value", produces = [MediaType.APPLICATION_JSON])
    fun queryJsonData(
        @QueryValue("name") name: String,
        @QueryValue("value") value: Optional<Int>
    ): String {
        val requestData = MyRequestData(name, value)
        return "Received JSON data: $requestData"
    }

    @Get("/{value}/{index}")
    fun pathVariables(
        @PathVariable value: String,
        @PathVariable index: Int
    ): String {
        return "Item: $value number: $index"
    }


    @Serdeable.Serializable
    data class Data(
        val type: String,
        val name: String,
        val price: Double
    )

    private val rawData = listOf(
        Data("Fruit", "Apple", 1.5),
        Data("Fruit", "Banana", 0.8),
        Data("Fruit", "Orange", 2.0),
        Data("Fruit", "Grapes", 3.5),
        Data("Fruit", "Watermelon", 5.0),
        Data("Fruit", "Pineapple", 2.2),
        Data("Fruit", "Mango", 4.0),
        Data("Dessert", "Cheesecake", 10.5),
        Data("Dessert", "Cupcake", 2.5),
        Data("Dessert", "Brownie", 3.8),
        Data("Fruit", "Strawberry", 1.2),
        Data("Fruit", "Kiwi", 2.8),
        Data("Fruit", "Peach", 2.5),
        Data("Fruit", "Blueberry", 1.0),
        Data("Fruit", "Cherry", 1.8),
        Data("Fruit", "Lemon", 1.3),
        Data("Fruit", "Pomegranate", 3.0),
        Data("Fruit", "Raspberry", 1.5),
        Data("Dessert", "Chocolate Cake", 12.0),
        Data("Dessert", "Ice Cream", 4.5)
    )

    // * GET Request Query Parameters
    @Get("/filter{?type,name,price}")
    fun getFruitName(
        @QueryValue("type") type: Optional<String>,
        @QueryValue("name") name: Optional<String>,
        @QueryValue("price") price: Optional<Double>,
    ): List<Data> {
        return rawData.stream()
            .filter { item ->
                // กรองตาม type (หาก type ไม่ว่าง)
                val typeMatch = !type.isPresent || item.type.equals(type.get(), ignoreCase = true)

                // กรองตาม name (หาก name ไม่ว่าง)
                val nameMatch = !name.isPresent || item.name.equals(name.get(), ignoreCase = true)

                // กรองตามราคา (หาก price ไม่ว่าง)
                val priceMatch = !price.isPresent || item.price <= price.get()

                return@filter typeMatch && nameMatch && priceMatch
            }
            .collect(Collectors.toList())
    }




}
