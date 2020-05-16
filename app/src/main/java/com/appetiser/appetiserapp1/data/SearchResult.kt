package com.appetiser.appetiserapp1.data

import io.realm.RealmList
import io.realm.RealmObject

open class SearchResult(
    var resultCount: Int = 0,
    var tracks: RealmList<Track> = RealmList()
) : RealmObject()