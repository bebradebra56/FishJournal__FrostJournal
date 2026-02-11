package com.fishjorunal.sofircl.lpnyht.data.repo

import android.util.Log
import com.fishjorunal.sofircl.lpnyht.domain.model.FrostJournalEntity
import com.fishjorunal.sofircl.lpnyht.domain.model.FrostJournalParam
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication.Companion.FROST_JOURNAL_MAIN_TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer



private const val FROST_JOURNAL_MAIN = "https://fiishjournal.com/config.php"

class FrostJournalRepository {


    private val ktorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }

    }

    suspend fun frostJournalGetClient(
        frostJournalParam: FrostJournalParam,
        frostJournalConversion: MutableMap<String, Any>?
    ): FrostJournalEntity? =
        withContext(Dispatchers.IO) {
            ktorClient.plugin(HttpSend).intercept { request ->
                Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: Intercept body ${request.body}")
                execute(request)
            }
            val json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            Log.d(
                FROST_JOURNAL_MAIN_TAG,
                "Ktor: conversation json: ${frostJournalConversion.toString()}"
            )
            val body = mergeToFlatJson(
                json = json,
                param = frostJournalParam,
                conversation = frostJournalConversion
            )
            Log.d(
                FROST_JOURNAL_MAIN_TAG,
                "Ktor: request json: $body"
            )
            return@withContext try {
                val response = ktorClient.post(FROST_JOURNAL_MAIN) {
                    contentType(ContentType.Application.Json) // обязательно JSON
                    accept(ContentType.Application.Json)
                    setBody(body) // JsonObject
                }
                val code = response.status.value
                Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: Request status code: $code")
                if (code == 200) {
                    val rawBody = response.bodyAsText() // читаем ответ как текст
                    val frostJournalEntity = Json { ignoreUnknownKeys = true }
                        .decodeFromString(FrostJournalEntity.serializer(), rawBody)
                    Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: Get request success")
                    Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: $frostJournalEntity")
                    frostJournalEntity
                } else {
                    Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: Status code invalid, return null")
                    Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: ${response.body<String>()}")
                    null
                }

            } catch (e: Exception) {
                Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: Get request failed")
                Log.d(FROST_JOURNAL_MAIN_TAG, "Ktor: ${e.message}")
                null
            }
        }

    private inline fun <reified T> Json.encodeToJsonObject(value: T): JsonObject =
        encodeToJsonElement(serializer(), value).jsonObject

    private inline fun <reified T> mergeToFlatJson(
        json: Json,
        param: T,
        conversation: Map<String, Any>?
    ): JsonObject {

        val paramJson = json.encodeToJsonObject(param)

        return buildJsonObject {
            // поля из param
            paramJson.forEach { (key, value) ->
                put(key, value)
            }

            // динамические поля
            conversation?.forEach { (key, value) ->
                put(key, anyToJsonElement(value))
            }
        }
    }

    private fun anyToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> buildJsonObject {
                value.forEach { (k, v) ->
                    if (k is String) {
                        put(k, anyToJsonElement(v))
                    }
                }
            }
            is List<*> -> buildJsonArray {
                value.forEach {
                    add(anyToJsonElement(it))
                }
            }
            else -> JsonPrimitive(value.toString())
        }
    }


}
