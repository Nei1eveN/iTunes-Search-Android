package com.appetiser.appetiserapp1.ui.fragments

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.appetiser.appetiserapp1.core.MvRxViewModel
import com.appetiser.appetiserapp1.data.model.Track
import com.appetiser.appetiserapp1.data.repository.TrackRepository
import io.realm.Realm
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class TrackState(
    val tracks: List<Track> = emptyList(),
    val lastViewedTrackId: Int? = null,
    val isRefreshing: Boolean = false
) : MvRxState

class TrackListFragmentVM(
    initialState: TrackState,
    private val repository: TrackRepository
) : MvRxViewModel<TrackState>(initialState) {

    init {
        viewModelScope.launch {
            repository.getTracks().collect { setTracks(it) }
        }

    }

    private fun setTracks(tracks: List<Track>) {
        setState {
            copy(tracks = tracks)
        }
    }

    companion object : MvRxViewModelFactory<TrackListFragmentVM, TrackState> {
        override fun initialState(viewModelContext: ViewModelContext): TrackState? {
            return super.initialState(viewModelContext)
        }

        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackState
        ): TrackListFragmentVM? {
            val realm = Realm.getDefaultInstance()
            return TrackListFragmentVM(
                state,
                TrackRepository(realm)
            )
        }
    }

}