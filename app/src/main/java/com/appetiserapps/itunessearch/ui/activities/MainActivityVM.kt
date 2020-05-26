package com.appetiserapps.itunessearch.ui.activities

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
import com.appetiserapps.itunessearch.extensions.getLastPageId
import com.appetiserapps.itunessearch.extensions.getLastPageSharedPreferences
import com.appetiserapps.itunessearch.extensions.getLastSearchKeyword
import com.appetiserapps.itunessearch.extensions.getLastTrackId
import com.appetiserapps.itunessearch.extensions.getNavigateFromHome
import com.appetiserapps.itunessearch.extensions.getSearchResultJSONString
import com.appetiserapps.itunessearch.extensions.getSearchResultList
import com.appetiserapps.itunessearch.extensions.getViewMoreList
import com.appetiserapps.itunessearch.extensions.getViewMoreTracks
import com.appetiserapps.itunessearch.extensions.groupIntoTracksByDefaultDateFormat
import com.appetiserapps.itunessearch.extensions.navigateTo
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

/**
 * data class for setting the [title] as header and [tracks] as content
 * @param title title of the [ExpandableTrackItem]
 * @param tracks content of the [ExpandableTrackItem]
 * */
data class ExpandableTrackItem(
    var title: String = "",
    var tracks: List<Track> = emptyList()
)

/**
 * args for querying the [Track] object from the repository
 * @param trackId id required for querying the object
 * */
@Parcelize
data class TrackDetailArgs(val trackId: Int? = null) : Parcelable

/**
 * args for initializing the values to state to avoid changes when device gets rotated
 * @param trackId last record navigated
 * @param lastPageId last page navigated
 * @param lastSearchKey last word user searched
 * @param lastSearchedList last result of the list from the API upon search
 * */
@Parcelize
data class MainActivityArgs(
    val trackId: Int? = null,
    val lastPageId: Int? = null,
    val lastSearchKey: String? = null,
    val lastSearchedList: String? = null,
    val lastViewMoreTracks: String? = null,
    val navigatedFromHome: Boolean? = null
) : Parcelable

/**
 * State for the activity which can be accessed by fragments
 * @param tracks list of tracks visited by the user
 * @param searchedTracks list for searched tracks from [searchResult]
 * @param searchText user-input characters
 * @param searchResult response received from the API
 * @param isLoading loading value for showing refresh indicators
 * @param item [Track] object value for selected item from the list
 * */
data class TrackState(
    val tracks: List<Track> = emptyList(),
    val searchedTracks: List<Track> = emptyList(),
    val searchText: String = "",
    val searchResult: SearchResult = SearchResult(),
    val isLoading: Boolean = false,
    val item: Track? = null,
    val navigatedFromHome: Boolean = false,
    val expandableItems: List<ExpandableTrackItem> = emptyList(),
    val selectedDateTitle: String? = null,
    val isExpanded: Boolean = false
) : MvRxState

class MainActivityVM(
    initialState: TrackState,
    private val repository: TrackRepository,
    private val navigationController: NavController,
    mainActivityArgs: MainActivityArgs
) : MvRxViewModel<TrackState>(initialState) {

    init {
        viewModelScope.launch {
            repository.getTracks().collect { list ->
                setTracks(list.sortedByDescending { it.date })
            }
        }

        with(mainActivityArgs) {
            when {
                (trackId != null) && lastPageId == R.id.trackDetailFragment -> navigationController.navigateTo(lastPageId, TrackDetailArgs(trackId = trackId))
                (lastSearchKey != null && lastSearchKey.isNotEmpty()) && (lastSearchedList != null && lastSearchedList.isNotEmpty()) && lastPageId != null && lastPageId == R.id.trackSearchFragment -> {
                    viewModelScope.launch {
                        setState {
                            copy(searchResult = SearchResult(lastSearchedList.getSearchResultList().size, lastSearchedList.getSearchResultList()))
                        }
                        setSearchTerm(lastSearchKey)
                    }
                    navigationController.navigate(lastPageId)
                }
                (lastViewMoreTracks != null && lastViewMoreTracks.isNotEmpty()) &&
                    lastPageId != null && lastPageId == R.id.showMoreTrackFragment -> {
                    viewModelScope.launch {
                        setSearchedTracks(lastViewMoreTracks.getViewMoreList())
                    }
                    navigatedFromHome?.let { setNavigateFromHome(it) }
                    navigationController.navigate(lastPageId)
                }
                navigatedFromHome == true && lastPageId == R.id.showMoreTrackFragment -> {
                    setNavigateFromHome(navigatedFromHome)
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
            copy(
                tracks = tracks,
                expandableItems = tracks.groupIntoTracksByDefaultDateFormat()
            )
        }
    }

    /**
     * changes isExpanded value to state
     * */
    fun toggleExpanded() {
        setState {
            copy(isExpanded = !isExpanded)
        }
    }

    /**
     * set selectedDateTitle value to state
     * @param title title from expandable item [ExpandableTrackItem]
     * */
    fun setSelectedTitle(title: String?) {
        setState {
            copy(
                selectedDateTitle = title
            )
        }
    }

    /**
     * search for the term the user inputs
     * */
    fun searchFor(term: String) {
        setSearchTerm(term)
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
     * set searched text to state
     * @param term user-input characters
     * */
    private fun setSearchTerm(term: String) {
        setState {
            copy(searchText = term)
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
                    val updatedBook = repository.addItem(audioBook)?.toUnmanaged()
                    setState {
                        copy(item = updatedBook)
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
                } else {
                    otherTrack.apply {
                        previouslyVisited = true
                        date = Date()
                    }
                    val updatedTrack = repository.addItem(otherTrack)?.toUnmanaged()
                    setState {
                        copy(item = updatedTrack)
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
     * set searchedTracks value to state
     * @param searchedTracks value passed from tapping 'MORE' button on title header
     * */
    fun setSearchedTracks(searchedTracks: List<Track>) {
        setState {
            copy(searchedTracks = searchedTracks)
        }
    }

    /**
     * set navigatedFromHome value to state
     * @param navigatedFromHome to be set when page is from TrackListFragment or TrackSearchFragment from tapping 'MORE' button on title header
     * */
    fun setNavigateFromHome(navigatedFromHome: Boolean) {
        setState {
            copy(navigatedFromHome = navigatedFromHome)
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
            val sharedPref = viewModelContext.activity.getLastPageSharedPreferences()
            val trackId = sharedPref.getLastTrackId()
            val lastNavigatedPageId = sharedPref.getLastPageId()
            val lastSearchKey = sharedPref.getLastSearchKeyword()
            val lastSearchedTrackString = sharedPref.getSearchResultJSONString()
            val lastViewMoreTracks = sharedPref.getViewMoreTracks()
            val navigatedFromHome = sharedPref.getNavigateFromHome()
            val mainActivityArgs = viewModelContext.args as? MainActivityArgs ?: MainActivityArgs(trackId, lastNavigatedPageId, lastSearchKey, lastSearchedTrackString, lastViewMoreTracks, navigatedFromHome)
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
