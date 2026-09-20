package com.tesis_pro.tenderapp.domain.repository

import com.tesis_pro.tenderapp.domain.model.AuthSession

interface AuthSessionStore : SessionTokenProvider {
    fun getSession(): AuthSession?

    fun saveSession(session: AuthSession)

    fun clearSession()
}
