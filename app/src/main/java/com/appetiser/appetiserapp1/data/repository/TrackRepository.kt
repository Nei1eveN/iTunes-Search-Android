package com.appetiser.appetiserapp1.data.repository

import com.appetiser.appetiserapp1.core.dao.RealmBaseRepository
import com.appetiser.appetiserapp1.data.dao.TrackDao
import com.appetiser.appetiserapp1.extensions.asCallbackFlow
import io.realm.Realm
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TrackRepository(
    realm: Realm,
    private val trackDao: TrackDao = TrackDao(realm)
) : RealmBaseRepository(realm) {

    fun getTracks() = trackDao.findAllAsync().asCallbackFlow()


}