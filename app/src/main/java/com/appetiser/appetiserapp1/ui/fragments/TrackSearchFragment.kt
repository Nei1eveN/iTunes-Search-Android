package com.appetiser.appetiserapp1.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.appetiser.appetiserapp1.BindableTrackGridBindingModel_
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.bindableEmptyScreen
import com.appetiser.appetiserapp1.bindableHeaderViewMore
import com.appetiser.appetiserapp1.bindableTrackNormal
import com.appetiser.appetiserapp1.databinding.FragmentTrackSearchBinding
import com.appetiser.appetiserapp1.ui.activities.MainActivityVM
import com.appetiser.appetiserapp1.ui.activities.TrackState
import com.google.android.material.snackbar.Snackbar
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.extensions.hideKeyboard
import com.nei1even.adrcodingchallengelibrary.core.extensions.makeSafeSnackbar
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController

/**
 * A simple [Fragment] subclass.
 */
class TrackSearchFragment : EpoxyFragment<FragmentTrackSearchBinding>() {

    private val viewModel: MainActivityVM by activityViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override val layoutId: Int
        get() = R.layout.fragment_track_search

    override fun epoxyController() = simpleController(viewModel) { state ->
        if (state.searchText.isEmpty()) {
            bindableEmptyScreen {
                id("emptyScreen")
                text(getString(R.string.search_track))
                buttonVisible(false)
                onClick { _ -> }
            }
        }

        val searchTracks = state.searchResult.tracks
        if (searchTracks.isNotEmpty()) {
            val audioBooks = searchTracks.filter { it.wrapperType == "audiobook" }
            val tracks = searchTracks.filter { it.wrapperType == "track" }

            if (audioBooks.isNotEmpty()) {
                val moreThanOne = audioBooks.size > 1
                val moreThanThree = audioBooks.size >= 3

                bindableHeaderViewMore {
                    id("audioBookHeader")
                    headerText(getString(R.string.audiobook))
                    showViewMore(moreThanThree)
                    onClick { view ->
                        Toast.makeText(view.context, getString(R.string.audiobook), Toast.LENGTH_SHORT).show()
                    }
                }

                if (moreThanThree) {
                    carousel {
                        id("audioBooksCarousel")
                        padding(Carousel.Padding.dp(0, 4, 0, 16, 1))
                        hasFixedSize(true)
                        audioBooks.map {
                            BindableTrackGridBindingModel_()
                                .id("audioBookGridId ${it.trackId}")
                                .imageUrl(it.artworkUrl100)
                                .trackTitle(it.collectionName)
                                .trackPrice("Collection Price: ${it.collectionPrice}")
                                .trackGenre(it.primaryGenreName)
                                .onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                }
                        }.let { models(it) }
                        numViewsToShowOnScreen(if (moreThanOne) 1f else 0f)
                    }
                } else {
                    audioBooks.forEach {
                        bindableTrackNormal {
                            id("audioBookNormalId ${it.trackId}")
                            imageUrl(it.artworkUrl100)
                            trackTitle(it.collectionName)
                            trackPrice("Collection Price: ${it.collectionPrice}")
                            trackPrice(it.primaryGenreName)
                            onClick { _ ->
                                viewModel.viewDetails(it)
                                navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                            }
                        }
                    }
                }
            }

            if (tracks.isNotEmpty()) {
                val featureMovie = tracks.filter { it.kind == "feature-movie" }
                val song = tracks.filter { it.kind == "song" }

                if (featureMovie.isNotEmpty()) {
                    val moreThanOne = featureMovie.size > 1
                    val moreThanThree = featureMovie.size >= 3

                    bindableHeaderViewMore {
                        id("featureMovieHeader")
                        headerText(getString(R.string.feature_movie))
                        showViewMore(moreThanThree)
                        onClick { view ->
                            Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("featureMovieCarousel")
                            padding(Carousel.Padding.dp(0, 4, 0, 16, 1))
                            hasFixedSize(true)
                            featureMovie.map {
                                BindableTrackGridBindingModel_()
                                    .id("movieId ${it.trackId}")
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(it.collectionName)
                                    .trackPrice("Collection Price: ${it.collectionPrice}")
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                            numViewsToShowOnScreen(if (moreThanOne) 1f else 0f)
                        }
                    } else {
                        featureMovie.forEach {
                            val title = if(it.trackName.isNotEmpty()) it.trackName else it.collectionName
                            val price = if(it.trackPrice > 0.0) it.trackPrice else it.collectionPrice

                            bindableTrackNormal {
                                id("movieId ${it.trackId}")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                trackPrice(price.toString())
                                trackPrice(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }
                }

                if (song.isNotEmpty()) {
                    val moreThanOne = song.size > 1
                    val moreThanThree = song.size >= 3

                    bindableHeaderViewMore {
                        id("songHeader")
                        headerText(getString(R.string.song))
                        showViewMore(moreThanThree)
                        onClick { view ->
                            Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("songCarousel")
                            padding(Carousel.Padding.dp(0, 4, 0, 16, 1))
                            hasFixedSize(true)
                            song.map {
                                BindableTrackGridBindingModel_()
                                    .id("songId ${it.trackId}")
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(it.collectionName)
                                    .trackPrice("Collection Price: ${it.collectionPrice}")
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                            numViewsToShowOnScreen(if (moreThanOne) 1f else 0f)
                        }
                    } else {
                        song.forEach {
                            val title = if(it.trackName.isNotEmpty()) it.trackName else it.collectionName
                            val price = if(it.trackPrice > 0.0) it.trackPrice else it.collectionPrice

                            bindableTrackNormal {
                                id("songId ${it.trackId}")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                trackPrice(price.toString())
                                trackPrice(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            val searchText = searchToolbar.searchText

            searchText.run {
                withState(viewModel) {
                    setText(it.searchText)

                    setOnEditorActionListener { _, actionId, _ ->
                        when(actionId) {
                            EditorInfo.IME_ACTION_SEARCH -> {
                                when {
                                    this.text.toString().isEmpty() -> {
                                        makeSafeSnackbar("Please enter a text", Snackbar.LENGTH_LONG)?.show()
                                        viewModel.setSearchTerm("")
                                    }
                                    else -> {
                                        activity?.hideKeyboard()
                                        viewModel.setSearchTerm(text.toString())
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    }
                }
            }

            viewModel.run {
                selectSubscribe(TrackState::isLoading) {
                    swipeRefresh.isRefreshing = it
                }

                /*selectSubscribe(TrackState::item) {
                    it?.let { track ->
                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment, TrackDetailArgs(track.trackId))
                    }
                }*/
            }
        }
    }
}
