package com.appetiser.appetiserapp1.data.network

import com.appetiser.appetiserapp1.data.model.SearchResult
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchAPI {

    @GET("/search")
    suspend fun searchTermWithCountryAndMedia(
        @Query("term") term: String,
        @Query("country") country: String,
        @Query("media") media: String = "all",
        @Query("limit") limit: Int = 50
    ): Response<SearchResult>

    @GET("/search")
    fun searchTerm(
        @Query("term") term: String
    ): Call<SearchResult>
}