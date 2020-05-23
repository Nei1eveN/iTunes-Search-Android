package com.appetiserapps.itunessearch.data.dao

import com.appetiserapps.itunessearch.core.dao.TrackBaseDao
import com.appetiserapps.itunessearch.data.model.Track
import com.nei1even.adrcodingchallengelibrary.core.extensions.find
import io.realm.Realm
import io.realm.kotlin.where

class TrackDao(private val realm: Realm) : TrackBaseDao<Track>(realm, Track::class.java) {

    override fun findById(id: Int): Track? = realm.find(id)

    fun findByCollectionId(collectionId: Int): Track? {
        return realm.where<Track>().equalTo("collectionId", collectionId).findFirst()
    }
}