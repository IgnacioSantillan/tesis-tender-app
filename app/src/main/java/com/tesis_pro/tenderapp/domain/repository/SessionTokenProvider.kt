package com.tesis_pro.tenderapp.domain.repository

interface SessionTokenProvider {
    fun getAccessToken(): String?
}
