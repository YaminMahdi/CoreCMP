package com.dora.user.di

import com.dora.user.BuildKonfig
import com.dora.user.utils.Constants
import com.dora.user.utils.log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ro.cosminmihu.ktor.monitor.ContentLength
import ro.cosminmihu.ktor.monitor.KtorMonitorLogging
import ro.cosminmihu.ktor.monitor.RetentionPeriod

val networkModule = module {
    single {
        HttpClient {
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        message.log("Ktor")
                    }
                }
            }
            install(KtorMonitorLogging) {
//                sanitizeHeader { header -> header == "Authorization" }
                filter { request -> !request.url.host.contains("cosminmihu.ro") }
                showNotification = true
                retentionPeriod = RetentionPeriod.OneWeek
                maxContentLength = ContentLength.Full
            }
            install(DefaultRequest) {
                url(BuildKonfig.BASE_URL)
                header(HttpHeaders.ContentType, Json)
                header("apiVersion", Constants.API_VERSION)
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }
    }
}