package com.example.udfapp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable

class NetworkService {
    companion object {
        private fun getClient() =
            HttpClient {
                defaultRequest {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(kotlinx.serialization.json.Json { this.ignoreUnknownKeys = true })
                    }
                }
            }

        suspend fun request(name: String, callback: (String, Error?) -> Unit): Job = coroutineScope {
            val request = Request("https://api.agify.io?name=$name")
            launch {
                try {
                    val response: HttpResponse = getClient().get(request.path)
                    val responseBody: String = response.receive()
                    callback(responseBody, null)
                } catch (error: Throwable) {
                    callback("", Error(error))
                }
            }
        }
    }
}

data class Request (
    val path: String
)

@Serializable
data class AgifyResponse(
    val name: String? = null,
    val age: Int? = null,
    val count: Int? = null
) {
    fun displayString() =
        "${name ?: ""} is likely ${age ?: 0} years old, based on ${count ?: 0} others with that name."
}
