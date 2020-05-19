package com.appetiser.appetiserapp1.core.network

import okhttp3.Response
import retrofit2.HttpException

/**
 * Sealed class of HTTP result
 */
@Suppress("unused")
sealed class ApiResult<out T : Any> {
    /**
     * Successful result of request without errors
     */
    class Ok<out T : Any>(
        val value: T,
        override val response: Response
    ) : ApiResult<T>(), ResponseResult {
        override fun toString() = "Result.Ok{value=$value, response=$response}"
    }

    /**
     * HTTP error
     */
    class HttpError(
        override val exception: HttpException,
        override val response: Response
    ) : ApiResult<Nothing>(), ErrorResult, ResponseResult {
        override fun toString() = "Result.Error{exception=$exception}"
    }

    /**
     * Network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response
     */
    class Exception(
        override val exception: Throwable
    ) : ApiResult<Nothing>(), ErrorResult {
        override fun toString() = "Result.Exception{$exception}"
    }

}

/**
 * Interface for [ApiResult] classes with [okhttp3.Response]: [ApiResult.Ok] and [ApiResult.HttpError]
 */
interface ResponseResult {
    val response: Response
}

/**
 * Interface for [ApiResult] classes that contains [Throwable]: [ApiResult.HttpError] and [ApiResult.Exception]
 */
interface ErrorResult {
    val exception: Throwable
}

/**
 * Returns [ApiResult.Ok.value] or `null`
 */
fun <T : Any> ApiResult<T>.getOrNull(): T? = (this as? ApiResult.Ok)?.value

/**
 * Returns [ApiResult.Ok.value] or [default]
 */
fun <T : Any> ApiResult<T>.getOrDefault(default: T): T = getOrNull() ?: default

/**
 * Returns [ApiResult.Ok.value] or throw [throwable] or [ErrorResult.exception]
 */
fun <T : Any> ApiResult<T>.getOrThrow(throwable: Throwable? = null): T {
    return when (this) {
        is ApiResult.Ok -> value
        is ApiResult.HttpError -> throw throwable ?: exception
        is ApiResult.Exception -> throw throwable ?: exception
    }
}