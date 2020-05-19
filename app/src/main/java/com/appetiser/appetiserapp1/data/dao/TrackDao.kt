package com.appetiser.appetiserapp1.data.dao

import com.appetiser.appetiserapp1.core.dao.TrackBaseDao
import com.appetiser.appetiserapp1.data.model.Track
import com.appetiser.appetiserapp1.extensions.find
import io.realm.Realm

class TrackDao(private val realm: Realm) : TrackBaseDao<Track>(realm, Track::class.java) {

    override fun findById(id: Int): Track? = realm.find(id)
}