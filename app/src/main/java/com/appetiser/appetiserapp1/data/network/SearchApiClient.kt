package com.appetiser.appetiserapp1.data.network

import android.util.Log
import com.appetiser.appetiserapp1.BuildConfig
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val HEADER_ACCEPT = "Accept"

class SearchApiClient : Interceptor {

    private val client: OkHttpClient
    private val retrofit: Retrofit

    private var service: SearchAPI? = null

    init {
        val logging =  LoggingInterceptor.Builder()
            .setLevel(if (BuildConfig.DEBUG) Level.BODY else Level.NONE)
            .log(Log.INFO)
            .request("Request")
            .response("Response")
            .addHeader("version", BuildConfig.VERSION_NAME)
            .build()

        client = OkHttpClient.Builder()
            .addNetworkInterceptor(this)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val urlBuilder = original.url.newBuilder()

        val builder: Request.Builder = original.newBuilder()
            .url(urlBuilder.build())
            .header(HEADER_ACCEPT, "application/json")

        val request = builder.build()

        return chain.proceed(request)
    }

    fun getService(): SearchAPI {
        if (service == null)
            service = retrofit.create(SearchAPI::class.java)
        return service!!
    }

    companion object {
        private var baseUrl = "https://itunes.apple.com/"
    }
}