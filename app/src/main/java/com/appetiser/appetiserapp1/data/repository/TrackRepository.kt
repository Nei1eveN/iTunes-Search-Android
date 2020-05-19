package com.appetiser.appetiserapp1.data.repository

import com.airbnb.mvrx.Async
import com.appetiser.appetiserapp1.core.awaitAsync
import com.appetiser.appetiserapp1.core.dao.RealmBaseRepository
import com.appetiser.appetiserapp1.data.dao.TrackDao
import com.appetiser.appetiserapp1.data.model.SearchResult
import com.appetiser.appetiserapp1.data.network.SearchApiClient
import com.appetiser.appetiserapp1.extensions.asCallbackFlow
import io.realm.Realm
import retrofit2.Call

class TrackRepository(
    realm: Realm,
    private val trackDao: TrackDao = TrackDao(realm),
    private val searchApiClient: SearchApiClient = SearchApiClient()
) : RealmBaseRepository(realm) {

    fun getTracks() = trackDao.findAllAsync().asCallbackFlow()

    fun searchTerm(term: String): Call<SearchResult> =
        searchApiClient.getService().searchTerm(term)

}