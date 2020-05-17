package com.appetiser.appetiserapp1.core

import com.appetiser.appetiserapp1.core.network.DateSerializer
import com.appetiser.appetiserapp1.core.network.GsonRealmBuilder
import com.appetiser.appetiserapp1.core.network.Networking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit

private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_ACCEPT = "Accept"
private const val HEADER_X_REQUESTED_FOR = "X-REQUESTED-FOR"

class SearchApiClient() : Interceptor {

    private val client: OkHttpClient
    private val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            /*logging.level = HttpLoggingInterceptor.Level
            .level = (if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
            .log(Log.INFO)
            .request("Request")
            .response("Response")
            .addHeader("version", BuildConfig.VERSION_NAME)
            .build()*/

        client = OkHttpClient.Builder()
            .addNetworkInterceptor(this)
            .connectionPool(Networking.pool)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val gson = GsonRealmBuilder.builder
            .registerTypeAdapter(Date::class.java,
                DateSerializer()
            )
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    companion object {
        private var baseUrl = "https://itunes.apple.com/"
    }
}