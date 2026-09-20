package com.tesis_pro.tenderapp.data.remote

object BackendNotificationApiClient {
    fun create(
        baseUrl: String = BackendConfig.baseUrl,
        enableBodyLogging: Boolean = false,
    ): TenderNotificationApi {
        return BackendApiClient.createRetrofit(
            baseUrl = baseUrl,
            enableBodyLogging = enableBodyLogging,
        ).create(TenderNotificationApi::class.java)
    }
}
