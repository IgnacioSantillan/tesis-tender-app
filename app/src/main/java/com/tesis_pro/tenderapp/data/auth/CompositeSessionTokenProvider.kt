package com.tesis_pro.tenderapp.data.auth

import com.tesis_pro.tenderapp.domain.repository.SessionTokenProvider

class CompositeSessionTokenProvider(
    private vararg val providers: SessionTokenProvider,
) : SessionTokenProvider {
    override fun getAccessToken(): String? {
        return providers.firstNotNullOfOrNull { provider -> provider.getAccessToken() }
    }
}
