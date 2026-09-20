package com.tesis_pro.tenderapp.data.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseAuthApiClient {
    fun create(config: SupabaseAuthConfig = SupabaseAuthConfig.fromBuildConfig()): SupabaseAuthApi {
        return Retrofit.Builder()
            .baseUrl("${config.projectUrl}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseAuthApi::class.java)
    }
}
