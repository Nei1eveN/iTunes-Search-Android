package com.appetiser.appetiserapp1.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*suspend inline fun <T> safeApiCall(
    crossinline body: suspend () -> T
): SafeApiResponseResult<T> {
    return try {
        // blocking block
        val users = withContext(Dispatchers.IO) {
            body()
        }
        SafeApiResponseResult.Success(users)
    } catch (e: Exception) {
        SafeApiResponseResult.Failure(e)
    }
}

sealed class SafeApiResponseResult<out H> {
    data class Success<out T>(val data: T)
        : SafeApiResponseResult<T>()
    data class Failure(val error: Throwable)
        : SafeApiResponseResult<Nothing>()
    object Pending : SafeApiResponseResult<Nothing>()
    object Complete : SafeApiResponseResult<Nothing>()
}*/

