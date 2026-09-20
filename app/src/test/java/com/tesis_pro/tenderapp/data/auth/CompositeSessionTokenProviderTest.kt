package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class CompositeSessionTokenProviderTest {
    @Test
    fun getAccessToken_returnsFirstAvailableToken() {
        val provider = CompositeSessionTokenProvider(
            FakeCompositeTokenProvider(null),
            FakeCompositeTokenProvider("session-token"),
            FakeCompositeTokenProvider("fallback-token"),
        )

        assertEquals("session-token", provider.getAccessToken())
    }

    @Test
    fun getAccessToken_returnsNullWhenEveryProviderIsEmpty() {
        val provider = CompositeSessionTokenProvider(
            FakeCompositeTokenProvider(null),
            FakeCompositeTokenProvider(null),
        )

        assertEquals(null, provider.getAccessToken())
    }
}

private class FakeCompositeTokenProvider(
    private val token: String?,
) : SessionTokenProvider {
    override fun getAccessToken(): String? = token
}
