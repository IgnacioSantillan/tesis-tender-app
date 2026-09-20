package com.tesis_pro.tenderapp.data.notification

import com.tesis_pro.tenderapp.domain.repository.AuthSessionStore

class StoredSessionPushRegistration(
    private val sessionStore: AuthSessionStore,
    private val postLoginPushRegistrar: PostLoginPushRegistrar,
) {
    suspend fun registerIfSessionExists(): PushRegistrationResult? {
        val session = sessionStore.getSession() ?: return null
        return postLoginPushRegistrar.registerAfterLogin(session)
    }
}
