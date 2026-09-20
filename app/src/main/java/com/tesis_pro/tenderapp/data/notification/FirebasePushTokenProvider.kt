package com.tesis_pro.tenderapp.data.notification

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface PushTokenProvider {
    suspend fun currentToken(): Result<String>
}

class FirebasePushTokenProvider(
    private val tokenStore: FirebasePushTokenStore,
) : PushTokenProvider {
    override suspend fun currentToken(): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!continuation.isActive) return@addOnCompleteListener

                val token = task.result
                val result = if (task.isSuccessful && !token.isNullOrBlank()) {
                    tokenStore.saveLatestToken(token)
                    Result.success(token)
                } else {
                    Result.failure(
                        task.exception ?: IllegalStateException("Firebase Messaging returned an empty token."),
                    )
                }

                continuation.resume(result)
            }
        }
    }
}
