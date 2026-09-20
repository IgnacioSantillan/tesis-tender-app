package com.tesis_pro.tenderapp.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BuildConfigSessionTokenProviderTest {
    @Test
    fun getAccessToken_returnsTrimmedTokenWhenConfigured() {
        val provider = BuildConfigSessionTokenProvider(tokenSource = { "  token-1  " })

        assertEquals("token-1", provider.getAccessToken())
    }

    @Test
    fun getAccessToken_returnsNullWhenTokenIsBlank() {
        val provider = BuildConfigSessionTokenProvider(tokenSource = { "   " })

        assertNull(provider.getAccessToken())
    }
}
