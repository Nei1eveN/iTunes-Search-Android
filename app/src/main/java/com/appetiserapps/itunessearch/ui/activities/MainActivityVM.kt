package com.appetiserapps.itunessearch.ui.activities

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.data.model.SearchResult
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.data.repository.TrackRepository
import com.appetiserapps.itunessearch.extensions.navigateTo
import com.appetiserapps.itunessearch.utils.Constants
import com.nei1even.adrcodingchallengelibrary.core.extensions.toUnmanaged
import com.nei1even.adrcodingchallengelibrary.core.mvrx.MvRxViewModel
import com.nei1even.adrcodingchallengelibrary.core.network.awaitAsync
import io.realm.Realm
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

@Parcelize
data class TrackDetailArgs(val trackId: Int = 0) : Parcelable

@Parcelize
data class MainActivityArgs(val trackId: Int = 0, val lastPageId: Int = 0) : Parcelable

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
    val searchedTracks: List<Track> = emptyList(),
    val searchText: String = "",
    val searchResult: SearchResult = SearchResult(),
    val isLoading: Boolean = false,
    val item: Track? = null
) : MvRxState

class MainActivityVM(
    initialState: TrackState,
    private val repository: TrackRepository,
    private val navigationController: NavController,
    mainActivityArgs: MainActivityArgs
) : MvRxViewModel<TrackState>(initialState) {

    init {
        viewModelScope.launch {
            repository.getTracks().collect {
                setTracks(it)
            }
        }

        with(mainActivityArgs) {
            when {
                trackId > 0 && lastPageId == R.id.trackDetailFragment -> {
                    navigationController.navigateTo(lastPageId, TrackDetailArgs(trackId = trackId))
                }
                lastPageId > 0 && lastPageId == R.id.trackSearchFragment -> {
                    navigationController.navigate(lastPageId)
                }
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
                                        artworkUrl100 = artworkUrl100.replace("100x100bb.jpg", "1000x1000bb.jpg")
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
     * @param trackId last viewed id from Track object or saved from sharedPreferences
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
                } else {
                    audioBook.apply {
                        previouslyVisited = true
                        date = Date()
                    }
                    setState {
                        copy(item = audioBook)
                    }
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
                        date = Date()
                    }
                    setState {
                        copy(item = otherTrack)
                    }
                }
            }
        }
    }

    /**
     * set [Track] value to state with trackId
     * @param trackId to be queried from the repository
     * */
    fun viewDetails(trackId: Int) {
        viewModelScope.launch {
            val item = repository.findById(trackId)?.toUnmanaged()
            setState {
                copy(item = item)
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
            val navController = viewModelContext.activity.findNavController(R.id.nav_host_fragment)
            val sharedPref = viewModelContext.activity.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
            val trackId = sharedPref.getInt(Constants.TRACK_ID, 0)
            val lastNavigatedPageId = sharedPref.getInt(Constants.LAST_NAVIGATED_PAGE, 0)
            val mainActivityArgs = viewModelContext.args as? MainActivityArgs ?: MainActivityArgs(trackId, lastNavigatedPageId)
            val realm = Realm.getDefaultInstance()

            return MainActivityVM(
                state,
                TrackRepository(realm),
                navController,
                mainActivityArgs
            )
        }
    }

}