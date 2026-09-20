package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.domain.model.AuthSession
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Base64

data class SupabaseAuthCallback(
    val session: AuthSession,
    val type: String?,
)

object SupabaseAuthCallbackParser {
    fun parse(rawUri: String?): SupabaseAuthCallback? {
        val uri = rawUri?.takeIf { it.isNotBlank() } ?: return null
        val parts = runCatching {
            val parsed = URI(uri)
            parseKeyValueSegment(parsed.rawQuery) + parseKeyValueSegment(parsed.rawFragment)
        }.getOrElse { return null }
        val accessToken = parts["access_token"]?.takeIf { it.isNotBlank() } ?: return null
        val email = parts["email"]?.takeIf { it.isNotBlank() }
            ?: extractEmailFromJwt(accessToken)
            ?: return null

        return SupabaseAuthCallback(
            session = AuthSession(
                email = email,
                accessToken = accessToken,
                refreshToken = parts["refresh_token"]?.takeIf { it.isNotBlank() },
                expiresInSeconds = parts["expires_in"]?.toIntOrNull(),
                expiresAtEpochMillis = parts["expires_at"]?.toLongOrNull()?.let { it * 1_000L },
                emailVerified = true,
            ),
            type = parts["type"]?.takeIf { it.isNotBlank() },
        )
    }

    private fun parseKeyValueSegment(segment: String?): Map<String, String> {
        if (segment.isNullOrBlank()) return emptyMap()
        return segment
            .split("&")
            .mapNotNull { entry ->
                val index = entry.indexOf("=")
                if (index <= 0) return@mapNotNull null
                val key = decode(entry.substring(0, index))
                val value = decode(entry.substring(index + 1))
                key to value
            }
            .toMap()
    }

    private fun decode(value: String): String {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name())
    }

    private fun extractEmailFromJwt(token: String): String? {
        val payload = token.split(".").getOrNull(1) ?: return null
        val decoded = runCatching {
            String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8)
        }.getOrNull() ?: return null
        return EMAIL_PATTERN.find(decoded)?.groupValues?.getOrNull(1)
    }

    private val EMAIL_PATTERN = Regex("\"email\"\\s*:\\s*\"([^\"]+)\"")
}
