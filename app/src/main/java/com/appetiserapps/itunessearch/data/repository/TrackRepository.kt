package com.appetiserapps.itunessearch.data.repository

import com.appetiserapps.itunessearch.data.dao.TrackDao
import com.appetiserapps.itunessearch.data.model.SearchResult
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.data.network.SearchApiClient
import com.nei1even.adrcodingchallengelibrary.core.dao.RealmBaseRepository
import com.nei1even.adrcodingchallengelibrary.core.extensions.asCallbackFlow
import io.realm.Realm
import retrofit2.Call

class TrackRepository(
    realm: Realm,
    private val trackDao: TrackDao = TrackDao(realm),
    private val searchApiClient: SearchApiClient = SearchApiClient()
) : RealmBaseRepository(realm) {

    fun getTracks() = trackDao.findAllAsync().asCallbackFlow()

    fun findById(trackId: Int) = trackDao.findById(trackId)

    fun findByCollectionId(collectionId: Int) = trackDao.findByCollectionId(collectionId)

    fun addItem(item: Track) = trackDao.add(item)

    fun searchTerm(term: String): Call<SearchResult> =
        searchApiClient.getService().searchTerm(term)

}