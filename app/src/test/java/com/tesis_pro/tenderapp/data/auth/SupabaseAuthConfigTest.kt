package com.tesis_pro.tenderapp.data.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseAuthConfigTest {
    @Test
    fun isConfigured_returnsTrueWhenPublicValuesExist() {
        val config = SupabaseAuthConfig(
            projectUrl = "https://example.supabase.co",
            publishableKey = "sb_publishable_test",
            authRedirectUrl = "com.tesispro.tenderapp://auth-callback",
        )

        assertTrue(config.isConfigured)
    }

    @Test
    fun isConfigured_returnsFalseWhenPublicValuesAreMissing() {
        val config = SupabaseAuthConfig(
            projectUrl = "",
            publishableKey = "",
        )

        assertFalse(config.isConfigured)
    }

    @Test
    fun normalizeProjectUrl_trimsTrailingSlash() {
        assertEquals(
            "https://example.supabase.co",
            SupabaseAuthConfig.normalizeProjectUrl(" https://example.supabase.co/ "),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun normalizeProjectUrl_rejectsNonHttpsUrl() {
        SupabaseAuthConfig.normalizeProjectUrl("http://example.supabase.co")
    }

    @Test
    fun normalizeOptionalRedirectUrl_acceptsDeepLinkUri() {
        assertEquals(
            "com.tesispro.tenderapp://auth-callback",
            SupabaseAuthConfig.normalizeOptionalRedirectUrl(" com.tesispro.tenderapp://auth-callback/ "),
        )
    }

    @Test
    fun normalizeOptionalRedirectUrl_allowsBlankValue() {
        assertEquals("", SupabaseAuthConfig.normalizeOptionalRedirectUrl(" "))
    }

    @Test(expected = IllegalArgumentException::class)
    fun normalizeOptionalRedirectUrl_rejectsValuesWithoutScheme() {
        SupabaseAuthConfig.normalizeOptionalRedirectUrl("auth-callback")
    }
}
