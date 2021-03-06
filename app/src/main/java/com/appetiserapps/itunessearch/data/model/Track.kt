package com.appetiserapps.itunessearch.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

open class Track(
    @PrimaryKey
    var trackId: Int = 0,
    var artistId: Int = 0,
    var artistName: String = "",
    var artistViewUrl: String = "",
    var artworkUrl100: String = "",
    var artworkUrl30: String = "",
    var artworkUrl60: String = "",
    var collectionArtistId: Int = 0,
    var collectionArtistViewUrl: String = "",
    var collectionCensoredName: String = "",
    var collectionExplicitness: String = "",
    var collectionHdPrice: Double = 0.0,
    var collectionId: Int = 0,
    var collectionName: String = "",
    var collectionPrice: Double = 0.0,
    var collectionViewUrl: String = "",
    var contentAdvisoryRating: String = "",
    var copyright: String = "",
    var country: String = "",
    var currency: String = "",
    var description: String = "",
    var discCount: Int = 0,
    var discNumber: Int = 0,
    var hasITunesExtras: Boolean = false,
    var isStreamable: Boolean = false,
    var kind: String = "",
    var longDescription: String = "",
    var previewUrl: String = "",
    var primaryGenreName: String = "",
    var releaseDate: String = "",
    var shortDescription: String = "",
    var trackCensoredName: String = "",
    var trackCount: Int = 0,
    var trackExplicitness: String = "",
    var trackHdPrice: Double = 0.0,
    var trackHdRentalPrice: Double = 0.0,
    var trackName: String = "",
    var trackNumber: Int = 0,
    var trackPrice: Double = 0.0,
    var trackRentalPrice: Double = 0.0,
    var trackTimeMillis: Int = 0,
    var trackViewUrl: String = "",
    var wrapperType: String = "",
    var previouslyVisited: Boolean = false,
    var date: Date = Date()
) : RealmObject() {
    enum class WrapperType(val value: String) {
        AUDIOBOOK("audiobook"),
        TRACK("track")
    }

    enum class TrackKind(val value: String) {
        FEATURE_MOVIE("feature-movie"),
        SONG("song"),
        TV_EPISODE("tv-episode"),
        PODCAST("podcast")
    }
}