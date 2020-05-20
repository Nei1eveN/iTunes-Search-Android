package com.appetiser.appetiserapp1.ui.fragments

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.appetiser.appetiserapp1.core.MvRxViewModel
import com.appetiser.appetiserapp1.core.awaitAsync
import com.appetiser.appetiserapp1.data.model.SearchResult
import com.appetiser.appetiserapp1.data.model.Track
import com.appetiser.appetiserapp1.data.repository.TrackRepository
import com.appetiser.appetiserapp1.extensions.toUnmanaged
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class SearchState(
    val searchText: String = "",
    val searchResult: SearchResult = SearchResult(),
    val isLoading: Boolean = false,
    val item: Track? = null
) : MvRxState

class TrackSearchFragmentVM(
    initialState: SearchState,
    private val repository: TrackRepository
) : MvRxViewModel<SearchState>(initialState) {

    fun searchTrackWithCountry(term: String, country: String) {
        setState {
            copy(
                searchText = term
            )
        }
    }

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

    private fun setRefreshing(refreshing: Boolean) {
        setState {
            copy(isLoading = refreshing)
        }
    }

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
                } else {
                    otherTrack.apply {
                        previouslyVisited = true
                    }
                    setState {
                        copy(item = otherTrack)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.closeRealm()
    }

    companion object : MvRxViewModelFactory<TrackSearchFragmentVM, SearchState> {
        override fun initialState(viewModelContext: ViewModelContext): SearchState? {
            return super.initialState(viewModelContext)
        }

        override fun create(
            viewModelContext: ViewModelContext,
            state: SearchState
        ): TrackSearchFragmentVM? {
            val realm = Realm.getDefaultInstance()
            return TrackSearchFragmentVM(
                state,
                TrackRepository(realm)
            )
        }
    }
}