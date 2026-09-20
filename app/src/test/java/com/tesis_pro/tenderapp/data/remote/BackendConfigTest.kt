package com.tesis_pro.tenderapp.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class BackendConfigTest {
    @Test
    fun normalizeBaseUrl_addsTrailingSlash() {
        assertEquals(
            "http://10.0.2.2:3000/api/v1/",
            BackendConfig.normalizeBaseUrl("http://10.0.2.2:3000/api/v1"),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun normalizeBaseUrl_rejectsNonHttpUrls() {
        BackendConfig.normalizeBaseUrl("file://backend")
    }
}
