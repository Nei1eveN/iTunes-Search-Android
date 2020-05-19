package com.appetiser.appetiserapp1.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> safeApiCall(
    crossinline body: suspend () -> T
): ResponseResult<T> {
    return try {
        // blocking block
        val users = withContext(Dispatchers.IO) {
            body()
        }
        ResponseResult.Success(users)
    } catch (e: Exception) {
        ResponseResult.Failure(e)
    }
}

sealed class ResponseResult<out H> {
    data class Success<out T>(val data: T)
        : ResponseResult<T>()
    data class Failure(val error: Throwable)
        : ResponseResult<Nothing>()
    object Pending : ResponseResult<Nothing>()
    object Complete : ResponseResult<Nothing>()
}

