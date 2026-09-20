package com.tesis_pro.tenderapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendApiClient {
    fun create(
        baseUrl: String = BackendConfig.baseUrl,
        enableBodyLogging: Boolean = false,
    ): TenderBackendApi {
        return createRetrofit(baseUrl = baseUrl, enableBodyLogging = enableBodyLogging)
            .create(TenderBackendApi::class.java)
    }

    fun createPredictionApi(
        baseUrl: String = BackendConfig.baseUrl,
        enableBodyLogging: Boolean = false,
    ): TenderPredictionApi {
        return createRetrofit(baseUrl = baseUrl, enableBodyLogging = enableBodyLogging)
            .create(TenderPredictionApi::class.java)
    }

    internal fun createRetrofit(
        baseUrl: String,
        enableBodyLogging: Boolean,
    ): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = if (enableBodyLogging) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BackendConfig.normalizeBaseUrl(baseUrl))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
