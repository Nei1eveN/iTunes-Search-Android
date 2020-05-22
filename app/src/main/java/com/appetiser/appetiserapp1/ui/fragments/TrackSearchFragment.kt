package com.appetiser.appetiserapp1.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.appetiser.appetiserapp1.data.model.Track
import com.appetiser.appetiserapp1.databinding.FragmentTrackSearchBinding
import com.appetiser.appetiserapp1.ui.activities.MainActivityVM
import com.appetiser.appetiserapp1.ui.activities.TrackState
import com.appetiser.appetiserapp1.utils.Constants
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
            val audioBooks = searchTracks.filter { it.wrapperType == Track.WrapperType.AUDIOBOOK.value }
            val tracks = searchTracks.filter { it.wrapperType == Track.WrapperType.TRACK.value }

            // wrapperType: audiobook
            if (audioBooks.isNotEmpty()) {
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
                        paddingRes(R.dimen.view_pager_item_padding)
                        hasFixedSize(true)
                        audioBooks.map {
                            BindableTrackGridBindingModel_()
                                .id(it.trackId)
                                .imageUrl(it.artworkUrl100)
                                .trackTitle(it.collectionName)
                                .trackPrice("Collection Price: ${it.collectionPrice}")
                                .trackGenre(it.primaryGenreName)
                                .onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                }
                        }.let { models(it) }
                    }
                } else {
                    audioBooks.forEach {
                        bindableTrackNormal {
                            id(it.trackId)
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

            // wrapperType: track
            if (tracks.isNotEmpty()) {
                val featureMovie = tracks.filter { it.kind == Track.TrackKind.FEATURE_MOVIE.value }
                val song = tracks.filter { it.kind == Track.TrackKind.SONG.value }
                val tvEpisode = tracks.filter { it.kind == Track.TrackKind.TV_EPISODE.value }

                // kind: feature-movie
                if (featureMovie.isNotEmpty()) {
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
                            paddingRes(R.dimen.view_pager_item_padding)
                            featureMovie.map {
                                BindableTrackGridBindingModel_()
                                    .id(it.trackId)
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(it.collectionName)
                                    .trackPrice("Collection Price: ${it.collectionPrice}")
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        featureMovie.forEach {
                            val title = if(it.trackName.isNotEmpty()) it.trackName else it.collectionName
                            val price = if(it.trackPrice > 0.0) it.trackPrice else it.collectionPrice

                            bindableTrackNormal {
                                id(it.trackId)
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

                // kind: song
                if (song.isNotEmpty()) {
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
                            paddingRes(R.dimen.view_pager_item_padding)
                            song.map {
                                BindableTrackGridBindingModel_()
                                    .id(it.trackId)
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(it.collectionName)
                                    .trackPrice("Collection Price: ${it.collectionPrice}")
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        song.forEach {
                            val title = if(it.trackName.isNotEmpty()) it.trackName else it.collectionName
                            val price = if(it.trackPrice > 0.0) it.trackPrice else it.collectionPrice

                            bindableTrackNormal {
                                id(it.trackId)
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

                // kind: tv-episode
                if (tvEpisode.isNotEmpty()) {
                    val moreThanThree = tvEpisode.size >= 3

                    bindableHeaderViewMore {
                        id("tvEpisodeHeader")
                        headerText(getString(R.string.tv_episode))
                        showViewMore(moreThanThree)
                        onClick { view ->
                            Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("tvEpisodeCarousel")
                            paddingRes(R.dimen.view_pager_item_padding)
                            hasFixedSize(true)
                            tvEpisode.map {
                                BindableTrackGridBindingModel_()
                                    .id(it.trackId)
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(it.collectionName)
                                    .trackPrice("Collection Price: ${it.collectionPrice}")
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        tvEpisode.forEach {
                            val title = if(it.trackName.isNotEmpty()) it.trackName else it.collectionName
                            val price = if(it.trackPrice > 0.0) it.trackPrice else it.collectionPrice

                            bindableTrackNormal {
                                id(it.trackId)
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

        val sharedPreferences = activity?.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
        sharedPreferences?.let { sharedPref ->
            sharedPref.edit().let { editor ->
                findNavController().currentDestination?.id?.let { editor.putInt(Constants.LAST_NAVIGATED_PAGE, it) }
                editor.apply()
            }
        }

        binding.run {
            with(epoxyRecyclerView) {
                val layoutManager = LinearLayoutManager(context)
                setLayoutManager(layoutManager)
            }

            val searchText = searchToolbar.searchText

            searchText.run {
                withState(viewModel) {
                    setText(it.searchText)

                    setOnEditorActionListener { _, actionId, event ->
                        when {
                            actionId == EditorInfo.IME_ACTION_SEARCH || event.action == KeyEvent.ACTION_DOWN -> {
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
            }
        }
    }
}
