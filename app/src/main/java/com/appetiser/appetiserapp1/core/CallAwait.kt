package com.appetiser.appetiserapp1.core

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.appetiser.appetiserapp1.core.network.ApiResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume

/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return sealed class [ApiResult] object that can be
 *         casted to [Result.Ok] (success) or [Result.Error] (HTTP error)
 *         and [Result.Exception] (other errors)
 */
public suspend fun <T : Any> Call<T>.awaitResult(): ApiResult<T> {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                continuation.resumeWith(runCatching {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) {
                            ApiResult.Exception(NullPointerException("Response body is null"))
                        } else {
                            ApiResult.Ok(body, response.raw())
                        }
                    } else {
                        ApiResult.HttpError(HttpException(response), response.raw())
                    }
                })
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                // Don't bother with resuming the continuation if it is already cancelled.
                if (continuation.isCancelled) return
                continuation.resume(ApiResult.Exception(t))
            }
        })

        registerOnCompletion(continuation)
    }
}

/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return sealed class [Async] object that can be
 * casted to [Success] (success) or [Fail] (HTTP error)
 */
public suspend fun <T : Any> Call<T>.awaitAsync(): Async<T> {
    val result = awaitResult()
    return when (result) {
        is ApiResult.Ok -> Success(result.value)
        is ApiResult.Exception -> Fail(result.exception)
        is ApiResult.HttpError -> Fail(result.exception)
    }
}

private fun Call<*>.registerOnCompletion(continuation: CancellableContinuation<*>) {
    continuation.invokeOnCancellation {
        try {
            cancel()
        } catch (ex: Throwable) {
            //Ignore cancel exception
        }
    }
}