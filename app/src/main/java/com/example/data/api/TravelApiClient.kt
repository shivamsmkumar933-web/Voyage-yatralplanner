package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object TravelApiClient {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        // Redact authorization headers to protect private API key from device logcat
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
            redactHeader("X-API-Key")
            redactHeader("X-RapidAPI-Key")
            redactHeader("X-Goog-Api-Key")
        }

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("User-Agent", "Voyage-Android-App/1.0")

                // Inject protected unified key
                if (TravelApiConfig.isAllInOneKeyActive) {
                    val key = TravelApiConfig.allInOneApiKey
                    when (TravelApiConfig.apiProvider.uppercase()) {
                        "RAPIDAPI" -> {
                            requestBuilder.header("X-RapidAPI-Key", key)
                        }
                        "GOOGLE" -> {
                            requestBuilder.header("X-Goog-Api-Key", key)
                        }
                        else -> {
                            requestBuilder.header("Authorization", "Bearer $key")
                            requestBuilder.header("X-API-Key", key)
                        }
                    }
                }

                if (TravelApiConfig.isBackendProxyActive) {
                    requestBuilder.header("X-App-Platform", "Android-Client")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    fun createProxyApi(baseUrl: String): TravelBackendProxyApi? {
        if (baseUrl.isBlank()) return null

        val sanitizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return try {
            Retrofit.Builder()
                .baseUrl(sanitizedUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(TravelBackendProxyApi::class.java)
        } catch (_: Exception) {
            null
        }
    }
}
