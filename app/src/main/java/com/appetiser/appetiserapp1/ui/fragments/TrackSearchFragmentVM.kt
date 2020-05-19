package com.appetiser.appetiserapp1.ui.fragments

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.appetiser.appetiserapp1.core.MvRxViewModel
import com.appetiser.appetiserapp1.data.model.Track

data class SearchState(
    val searchText: String = "",
    val country: String = "",
    val searchedTracks: Async<List<Track>> = Uninitialized
) : MvRxState

class TrackSearchFragmentVM(initialState: SearchState) : MvRxViewModel<SearchState>(initialState) {

    fun searchTrack(term: String, country: String) {
        setState {
            copy(
                searchText = term,
                country = country
            )
        }

    }
}