package com.tesis_pro.tenderapp.data.remote

sealed interface RemoteDataResult<out T> {
    data class Success<T>(val data: T) : RemoteDataResult<T>

    data class Error(
        val type: RemoteDataErrorType,
        val message: String,
    ) : RemoteDataResult<Nothing>
}

enum class RemoteDataErrorType {
    AUTHENTICATION,
    NOT_FOUND,
    NETWORK,
    SERVER,
    INVALID_RESPONSE,
    UNKNOWN,
}
