package com.example

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.serde.annotation.Serdeable
import java.util.*
import java.util.stream.Collectors

/**
 * This class represents a controller that handles API requests.
 *
 * @constructor Creates a new instance of MyController with the specified base path.
 * @param basePath The base path for the API endpoints handled by this controller.
 */
@Controller("/api")
class MyController {

    /**
     * Retrieves the User-Agent and Hellodata from the given HttpRequest.
     *
     * @param request The HttpRequest object.
     * @return A string containing the User-Agent and Hellodata from the request.
     */
    @Status(HttpStatus.OK)
    @Get
    @ExecuteOn(TaskExecutors.IO)
    fun getData(request: HttpRequest<*>): String {
        val user_agent = request.headers.get("User-Agent")
        val hello_data = request.headers.get("Hellodata")

        return "User-Agent of the request is: $user_agent \nHellodata: $hello_data"
    }

    /**
     * Represents a request data object.
     *
     * @property name The name of the request data.
     * @property value The optional value of the request data.
     */
    @Introspected
    data class MyRequestData(
        val name: String,
        val value: Optional<Int>
    )

    /**
     * Queries the JSON data based on the provided parameters.
     *
     * @param name The name parameter for querying the JSON data.
     * @param value The optional value parameter for querying the JSON data.
     * @return A string representation of the JSON data retrieved using the provided parameters.
     */
    @Get(
        value = "/query-value",
        produces = [MediaType.APPLICATION_JSON]
    )
    fun queryJsonData(
        @QueryValue("name") name: String,
        @QueryValue("value") value: Optional<Int>
    ): String {
        val requestData = MyRequestData(name, value)
        return "Received JSON data: $requestData"
    }


    /**
     * Retrieves the item and number based on the provided path variables.
     *
     * @param value The value of the item.
     * @param index The index of the number.
     * @return A string representation of the item and number.
     */
    @Get("/{value}/{index}")
    @ExecuteOn(TaskExecutors.IO)
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
    @Get("/filter{?type,name,price}", produces = [MediaType.APPLICATION_JSON])
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
