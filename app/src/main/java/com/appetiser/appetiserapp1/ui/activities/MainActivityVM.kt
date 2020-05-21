package com.appetiser.appetiserapp1.ui.activities

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.appetiser.appetiserapp1.data.model.SearchResult
import com.appetiser.appetiserapp1.data.model.Track
import com.appetiser.appetiserapp1.data.repository.TrackRepository
import com.nei1even.adrcodingchallengelibrary.core.extensions.toUnmanaged
import com.nei1even.adrcodingchallengelibrary.core.mvrx.MvRxViewModel
import com.nei1even.adrcodingchallengelibrary.core.network.awaitAsync
import io.realm.Realm
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Parcelize
data class TrackDetailArgs(val trackId: Int) : Parcelable

/**
 * State for the activity which can be accessed by fragments
 * @param tracks list of tracks visited by the user
 * @param searchText user-input characters
 * @param searchResult response received from the API
 * @param isLoading loading value for showing refresh indicators
 * @param item [Track] object value for selected item from the list
 * */
data class TrackState(
    val lastViewedTrackId: Int? = null,
    val tracks: List<Track> = emptyList(),
    val searchText: String = "",
    val searchResult: SearchResult = SearchResult(),
    val isLoading: Boolean = false,
    val item: Track? = null
) : MvRxState

class MainActivityVM(
    initialState: TrackState,
    private val repository: TrackRepository
) : MvRxViewModel<TrackState>(initialState) {

    init {
        viewModelScope.launch {
            repository.getTracks().collect {
                setTracks(it)
            }
        }
    }

    /**
     * set fetched tracks to state
     * @param tracks non-filtered tracks saved from the repository
     * */
    private fun setTracks(tracks: List<Track>) {
        setState {
            copy(tracks = tracks)
        }
    }

    /**
     * set searched text to state
     * @param term user-input characters
     * */
    fun setSearchTerm(term: String) {
        setState {
            copy(searchText = term)
        }
        setRefreshing(true)
        viewModelScope.launch {
            when (val result = repository.searchTerm(term).awaitAsync()) {
                is Success -> {
                    withContext(Dispatchers.Main) {
                        setState {
                            result.invoke().tracks.apply {
                                forEach {
                                    it.apply {
                                        artworkUrl100 = artworkUrl100.replace("100x100bb.jpg", "360x360bb.jpg")
                                    }
                                }
                            }
                            copy(searchResult = result.invoke())
                        }
                        setRefreshing(false)
                    }
                }
                is Fail -> {
                    setRefreshing(false)
                }
            }
        }
    }

    /**
     * set last viewed trackId to state
     * @param trackId last viewed id from Track object
     * */
    private fun setLastViewedTrackId(trackId: Int) {
        setState {
            copy(lastViewedTrackId = trackId)
        }
    }

    /**
     * set loading Boolean value to state
     * @param refreshing true/false
     * */
    private fun setRefreshing(refreshing: Boolean) {
        setState {
            copy(isLoading = refreshing)
        }
    }

    /**
     * set [Track] value and lastViewedTrackId to state
     * @param item selected item from the list
     * */
    fun viewDetails(item: Track) {
        viewModelScope.launch {
            if (item.wrapperType == "audiobook") {
                val audioBook = repository.findByCollectionId(item.collectionId)?.toUnmanaged()
                if (audioBook == null) {
                    item.apply {
                        trackId = when (trackId) {
                            0 -> UUID.randomUUID().mostSignificantBits.toInt()
                            else -> trackId
                        }
                        previouslyVisited = true
                    }
                    val newAudioBook = repository.addItem(item)
                    setState {
                        copy(item = newAudioBook)
                    }
                    newAudioBook?.trackId?.let { setLastViewedTrackId(it) }
                } else {
                    audioBook.apply {
                        previouslyVisited = true
                    }
                    setState {
                        copy(item = audioBook)
                    }
                    setLastViewedTrackId(audioBook.trackId)
                }
            } else {
                val otherTrack = repository.findById(item.trackId)?.toUnmanaged()
                if (otherTrack == null) {
                    item.apply {
                        trackId = when (trackId) {
                            0 -> UUID.randomUUID().mostSignificantBits.toInt()
                            else -> trackId
                        }
                        previouslyVisited = true
                    }
                    val newTrack = repository.addItem(item)
                    setState {
                        copy(item = newTrack)
                    }
                    newTrack?.trackId?.let { setLastViewedTrackId(it) }
                } else {
                    otherTrack.apply {
                        previouslyVisited = true
                    }
                    setState {
                        copy(item = otherTrack)
                    }
                    setLastViewedTrackId(otherTrack.trackId)
                }
            }
        }
    }

    /**
     * clears and destroys instance
     * */
    override fun onCleared() {
        super.onCleared()
        repository.closeRealm()
    }

    companion object : MvRxViewModelFactory<MainActivityVM, TrackState> {
        override fun initialState(viewModelContext: ViewModelContext): TrackState? {
            return super.initialState(viewModelContext)
        }

        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackState
        ): MainActivityVM? {
            val realm = Realm.getDefaultInstance()
            return MainActivityVM(
                state,
                TrackRepository(realm)
            )
        }
    }

}