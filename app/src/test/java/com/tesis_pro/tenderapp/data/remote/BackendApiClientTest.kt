package com.tesis_pro.tenderapp.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BackendApiClientTest {
    @Test
    fun createRetrofit_usesNormalizedBaseUrl() {
        val retrofit = BackendApiClient.createRetrofit(
            baseUrl = "http://10.0.2.2:3000/api/v1",
            enableBodyLogging = false,
        )

        assertEquals("http://10.0.2.2:3000/api/v1/", retrofit.baseUrl().toString())
    }

    @Test
    fun create_returnsTypedBackendApi() {
        val api = BackendApiClient.create(
            baseUrl = "http://10.0.2.2:3000/api/v1/",
            enableBodyLogging = false,
        )

        assertNotNull(api)
    }
}
