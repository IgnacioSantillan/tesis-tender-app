package com.tesis_pro.tenderapp.data.auth

import java.nio.charset.StandardCharsets
import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SupabaseAuthCallbackParserTest {
    @Test
    fun parse_readsSessionFromFragmentAndJwtEmail() {
        val token = jwtWithEmail("user@example.com")
        val callback = SupabaseAuthCallbackParser.parse(
            "com.tesispro.tenderapp://auth-callback#access_token=$token" +
                "&refresh_token=refresh-1&expires_in=3600&expires_at=1783735546&type=recovery"
        )

        assertNotNull(callback)
        assertEquals("user@example.com", callback?.session?.email)
        assertEquals(token, callback?.session?.accessToken)
        assertEquals("refresh-1", callback?.session?.refreshToken)
        assertEquals(3600, callback?.session?.expiresInSeconds)
        assertEquals(1_783_735_546_000L, callback?.session?.expiresAtEpochMillis)
        assertEquals(true, callback?.session?.emailVerified)
        assertEquals("recovery", callback?.type)
    }

    @Test
    fun parse_readsSessionFromQueryEmailWhenPresent() {
        val callback = SupabaseAuthCallbackParser.parse(
            "com.tesispro.tenderapp://auth-callback?access_token=token-1&email=user%40example.com"
        )

        assertNotNull(callback)
        assertEquals("user@example.com", callback?.session?.email)
        assertEquals("token-1", callback?.session?.accessToken)
    }

    @Test
    fun parse_returnsNullWhenAccessTokenIsMissing() {
        assertNull(SupabaseAuthCallbackParser.parse("com.tesispro.tenderapp://auth-callback?type=signup"))
    }

    private fun jwtWithEmail(email: String): String {
        val header = encode("""{"alg":"none"}""")
        val payload = encode("""{"email":"$email"}""")
        return "$header.$payload."
    }

    private fun encode(value: String): String {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(value.toByteArray(StandardCharsets.UTF_8))
    }
}
