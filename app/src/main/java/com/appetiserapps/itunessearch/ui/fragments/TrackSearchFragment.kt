package com.appetiserapps.itunessearch.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.appetiserapps.itunessearch.BindableTrackGridBindingModel_
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.bindableEmptyScreen
import com.appetiserapps.itunessearch.bindableHeaderViewMore
import com.appetiserapps.itunessearch.bindableTrackNormal
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.databinding.FragmentTrackSearchBinding
import com.appetiserapps.itunessearch.extensions.getLastPageSharedPreferences
import com.appetiserapps.itunessearch.extensions.saveLastPageId
import com.appetiserapps.itunessearch.extensions.saveLastSearchKeyword
import com.appetiserapps.itunessearch.extensions.saveSearchResultTracks
import com.appetiserapps.itunessearch.ui.activities.MainActivity
import com.appetiserapps.itunessearch.ui.activities.MainActivityVM
import com.appetiserapps.itunessearch.ui.activities.TrackState
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
            val audioBooks =
                searchTracks.filter { it.wrapperType == Track.WrapperType.AUDIOBOOK.value }
            val tracks = searchTracks.filter { it.wrapperType == Track.WrapperType.TRACK.value }

            // wrapperType: audiobook
            if (audioBooks.isNotEmpty()) {
                val moreThanThree = audioBooks.size >= 3

                bindableHeaderViewMore {
                    id("audioBookHeader")
                    headerText(getString(R.string.audiobook))
                    showViewMore(moreThanThree)
                    onClick { _ ->
                        viewModel.setSearchedTracks(audioBooks)
                        viewModel.setNavigateFromHome(false)
                        navigateTo(R.id.action_trackSearchFragment_to_showMoreTrackFragment)
                    }
                }

                if (moreThanThree) {
                    carousel {
                        id("audioBooksCarousel")
                        paddingRes(R.dimen.activity_horizontal_margin_default)
                        hasFixedSize(true)
                        audioBooks.map {
                            BindableTrackGridBindingModel_()
                                .id("audiobook${it}")
                                .imageUrl(it.artworkUrl100)
                                .trackTitle(it.collectionName)
                                .price("Collection Price: ${it.collectionPrice}")
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
                            id("audiobook${it}")
                            imageUrl(it.artworkUrl100)
                            trackTitle(it.collectionName)
                            price("Collection Price: ${it.collectionPrice}")
                            trackGenre(it.primaryGenreName)
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
                val podcasts = tracks.filter { it.kind == Track.TrackKind.PODCAST.value }

                // kind: feature-movie
                if (featureMovie.isNotEmpty()) {
                    val moreThanThree = featureMovie.size > 3

                    bindableHeaderViewMore {
                        id("featureMovieHeader")
                        headerText(getString(R.string.feature_movie))
                        showViewMore(moreThanThree)
                        onClick { _ ->
                            viewModel.setSearchedTracks(featureMovie)
                            viewModel.setNavigateFromHome(false)
                            navigateTo(R.id.action_trackSearchFragment_to_showMoreTrackFragment)
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("featureMovieCarousel")
                            paddingRes(R.dimen.activity_horizontal_margin_default)
                            featureMovie.map {
                                val title = when {
                                    it.trackName.isNotEmpty() -> it.trackName
                                    else -> it.trackCensoredName
                                }
                                val price = when {
                                    it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                    else -> "${it.collectionPrice} ${it.currency}"
                                }
                                BindableTrackGridBindingModel_()
                                    .id("movie${it}")
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(title)
                                    .price(price)
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        featureMovie.forEach {
                            val title = when {
                                it.trackName.isNotEmpty() -> it.trackName
                                else -> it.trackCensoredName
                            }
                            val price = when {
                                it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                else -> "${it.collectionPrice} ${it.currency}"
                            }

                            bindableTrackNormal {
                                id("movie${it}")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
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
                            viewModel.setSearchedTracks(song)
                            viewModel.setNavigateFromHome(false)
                            navigateTo(R.id.action_trackSearchFragment_to_showMoreTrackFragment)
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("songCarousel")
                            paddingRes(R.dimen.activity_horizontal_margin_default)
                            song.map {
                                val title = when {
                                    it.trackName.isNotEmpty() -> it.trackName
                                    else -> it.trackCensoredName
                                }
                                val price = when {
                                    it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                    else -> "${it.collectionPrice} ${it.currency}"
                                }
                                BindableTrackGridBindingModel_()
                                    .id("song$it")
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(title)
                                    .price(price)
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        song.forEach {
                            val title = when {
                                it.trackName.isNotEmpty() -> it.trackName
                                else -> it.trackCensoredName
                            }
                            val price = when {
                                it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                else -> "${it.collectionPrice} ${it.currency}"
                            }
                            bindableTrackNormal {
                                id("song$it")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
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
                        onClick { _ ->
                            viewModel.setSearchedTracks(tvEpisode)
                            viewModel.setNavigateFromHome(false)
                            navigateTo(R.id.action_trackSearchFragment_to_showMoreTrackFragment)
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("tvEpisodeCarousel")
                            paddingRes(R.dimen.activity_horizontal_margin_default)
                            hasFixedSize(true)
                            tvEpisode.map {
                                val title = when {
                                    it.trackName.isNotEmpty() -> it.trackName
                                    else -> it.trackCensoredName
                                }
                                val price = when {
                                    it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                    else -> "${it.collectionPrice} ${it.currency}"
                                }
                                BindableTrackGridBindingModel_()
                                    .id("tvSeries$it")
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(title)
                                    .price(price)
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        tvEpisode.forEach {
                            val title = when {
                                it.trackName.isNotEmpty() -> it.trackName
                                else -> it.trackCensoredName
                            }
                            val price = when {
                                it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                else -> "${it.collectionPrice} ${it.currency}"
                            }

                            bindableTrackNormal {
                                id("tvSeries$it")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }
                }

                // kind: podcast
                if (podcasts.isNotEmpty()) {
                    val moreThanThree = podcasts.size >= 3

                    bindableHeaderViewMore {
                        id("podcastHeader")
                        headerText(getString(R.string.podcast))
                        showViewMore(moreThanThree)
                        onClick { _ ->
                            viewModel.setSearchedTracks(podcasts)
                            viewModel.setNavigateFromHome(false)
                            navigateTo(R.id.action_trackSearchFragment_to_showMoreTrackFragment)
                        }
                    }

                    if (moreThanThree) {
                        carousel {
                            id("podcastCarousel")
                            paddingRes(R.dimen.activity_horizontal_margin_default)
                            hasFixedSize(true)
                            podcasts.map {
                                val title = when {
                                    it.trackName.isNotEmpty() -> it.trackName
                                    else -> it.trackCensoredName
                                }
                                val price = when {
                                    it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                    else -> "${it.collectionPrice} ${it.currency}"
                                }
                                BindableTrackGridBindingModel_()
                                    .id("podcast$it")
                                    .imageUrl(it.artworkUrl100)
                                    .trackTitle(title)
                                    .price(price)
                                    .trackGenre(it.primaryGenreName)
                                    .onClick { _ ->
                                        viewModel.viewDetails(it)
                                        navigateTo(R.id.action_trackSearchFragment_to_trackDetailFragment)
                                    }
                            }.let { models(it) }
                        }
                    } else {
                        podcasts.forEach {
                            val title = when {
                                it.trackName.isNotEmpty() -> it.trackName
                                else -> it.trackCensoredName
                            }
                            val price = when {
                                it.trackPrice > 0.0 -> "${it.trackPrice} ${it.currency}"
                                else -> "${it.collectionPrice} ${it.currency}"
                            }

                            bindableTrackNormal {
                                id("podcast$it")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
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

        val addButton = (activity as MainActivity).binding.addButton
        addButton.hide()

        val sharedPreferences = activity?.getLastPageSharedPreferences()
        findNavController().currentDestination?.id?.let { sharedPreferences.saveLastPageId(it) }

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
                                        makeSafeSnackbar(
                                            "Please enter a text",
                                            Snackbar.LENGTH_LONG
                                        )?.show()
                                        viewModel.searchFor("")
                                    }
                                    else -> {
                                        activity?.hideKeyboard()
                                        viewModel.searchFor(text.toString())
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

                selectSubscribe(TrackState::searchText) {
                    it.takeIf { it.isNotEmpty() }?.let {
                        sharedPreferences.saveLastSearchKeyword(it)
                    }
                }

                selectSubscribe(TrackState::searchResult) {
                    it.tracks.takeIf { it.isNotEmpty() }?.let { searchedTracks ->
                        sharedPreferences.saveSearchResultTracks(searchedTracks)
                    }
                }
            }
        }
    }
}
