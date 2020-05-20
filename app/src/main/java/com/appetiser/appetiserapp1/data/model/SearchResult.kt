package com.appetiser.appetiserapp1.data.model

import com.google.gson.annotations.SerializedName

data class SearchResult(
    var resultCount: Int = 0,
    @SerializedName("results")
    var tracks: List<Track> = emptyList()
)