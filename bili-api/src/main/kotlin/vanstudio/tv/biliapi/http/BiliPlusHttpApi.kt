package vanstudio.tv.biliapi.http

import vanstudio.tv.biliapi.http.entity.BiliResponse
import vanstudio.tv.biliapi.http.entity.biliplus.View
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object BiliPlusHttpApi {
    private var endPoint: String = "www.biliplus.com"
    private lateinit var client: HttpClient

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BrowserUserAgent()
            install(ContentNegotiation) {
                json(json)
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            install(HttpRequestRetry) {
                retryOnException(maxRetries = 2)
            }
            defaultRequest {
                url {
                    host = endPoint
                    protocol = URLProtocol.HTTPS
                }
            }
        }
    }

    suspend fun view(
        aid: Long,
        update: Boolean = true,
        accessKey: String? = null
    ): BiliResponse<View> {
        val result = client.get("/api/view") {
            parameter("id", aid)
            parameter("update", update)
            accessKey?.let { parameter("access_key", it) }
        }.bodyAsText()
        val resultJsonObject = json.parseToJsonElement(result).jsonObject
        return if (resultJsonObject.size == 3) {
            BiliResponse(
                code = resultJsonObject["code"]!!.jsonPrimitive.int,
                message = resultJsonObject["message"]!!.jsonPrimitive.content,
                ttl = resultJsonObject["ttl"]!!.jsonPrimitive.int,
                data = null
            )
        } else {
            BiliResponse(
                code = 0,
                message = "success",
                ttl = 0,
                result = json.decodeFromJsonElement<View>(resultJsonObject)
            )
        }
    }

    suspend fun getSeasonIdByAvid(
        aid: Long
    ): Int? {
        return runCatching {
            view(aid).getResponseData().bangumi?.seasonId?.toInt()
        }.onFailure {
            println("get season id by avid through biliplus failed: ${it.stackTraceToString()}")
        }.getOrDefault(null)
    }
}