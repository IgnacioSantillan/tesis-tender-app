package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.BuildConfig
import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider

class BuildConfigSessionTokenProvider(
    private val tokenSource: () -> String = { BuildConfig.TENDERAPP_ACCESS_TOKEN },
) : SessionTokenProvider {
    override fun getAccessToken(): String? {
        return tokenSource().trim().takeIf { it.isNotEmpty() }
    }
}
