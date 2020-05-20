package com.appetiser.appetiserapp1.data.dao

import com.appetiser.appetiserapp1.core.dao.TrackBaseDao
import com.appetiser.appetiserapp1.data.model.Track
import com.appetiser.appetiserapp1.extensions.find
import io.realm.Realm
import io.realm.kotlin.where

class TrackDao(private val realm: Realm) : TrackBaseDao<Track>(realm, Track::class.java) {

    override fun findById(id: Int): Track? = realm.find(id)

    fun findByCollectionId(collectionId: Int): Track? {
        return realm.where<Track>().equalTo("collectionId", collectionId).findFirst()
    }
}