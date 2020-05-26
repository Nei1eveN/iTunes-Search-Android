package com.appetiserapps.itunessearch.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.activityViewModel
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.bindableEmptyScreen
import com.appetiserapps.itunessearch.bindableExpandingHeader
import com.appetiserapps.itunessearch.bindableHeaderViewMore
import com.appetiserapps.itunessearch.bindableStaticDivider
import com.appetiserapps.itunessearch.bindableTrackNormal
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.databinding.FragmentTrackListBinding
import com.appetiserapps.itunessearch.extensions.getLastPageSharedPreferences
import com.appetiserapps.itunessearch.extensions.saveLastPageId
import com.appetiserapps.itunessearch.extensions.saveNavigateFromHome
import com.appetiserapps.itunessearch.extensions.saveViewMoreTracks
import com.appetiserapps.itunessearch.ui.activities.MainActivity
import com.appetiserapps.itunessearch.ui.activities.MainActivityVM
import com.appetiserapps.itunessearch.ui.activities.TrackState
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController
import kotlinx.android.synthetic.main.toolbar_main.view.toolbar

/**
 * A simple [Fragment] subclass.
 */
class ShowMoreTrackFragment : EpoxyFragment<FragmentTrackListBinding>() {

    private val viewModel: MainActivityVM by activityViewModel()

    override val layoutId: Int
        get() = R.layout.fragment_track_list

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override fun epoxyController() = simpleController(viewModel) { state ->
        if (state.navigatedFromHome) {
            state.expandableItems.forEach { dateTrackItem ->
                val dateTitle = dateTrackItem.title
                val isTitleSelected = state.isExpanded && dateTitle == state.selectedDateTitle
                bindableExpandingHeader {
                    id("date$dateTitle")
                    isExpanded(isTitleSelected)
                    headerText(dateTitle)
                    onClick { _ ->
                        viewModel.setSelectedTitle(dateTitle)
                        viewModel.toggleExpanded()
                    }
                }
                bindableStaticDivider {
                    id("divider${dateTitle}")
                    sidePadding(!isTitleSelected)
                    transparentBackground(true)
                }
                when {
                    isTitleSelected -> {
                        dateTrackItem.tracks.forEach {
                            val title = when (it.wrapperType) {
                                Track.WrapperType.AUDIOBOOK.value -> {
                                    when {
                                        it.collectionName.isNotEmpty() -> it.collectionName
                                        else -> it.collectionCensoredName
                                    }
                                }
                                else -> {
                                    when {
                                        it.trackName.isNotEmpty() -> it.trackName
                                        else -> it.trackCensoredName
                                    }
                                }
                            }
                            val price = when (it.wrapperType) {
                                Track.WrapperType.AUDIOBOOK.value -> "${it.collectionPrice} ${it.currency}"
                                else -> "${it.trackPrice} ${it.currency}"
                            }
                            bindableTrackNormal {
                                id("today${it.trackId}")
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (state.searchedTracks.isNotEmpty()) {
                val searchedTracks = state.searchedTracks
                val audioBooks = searchedTracks.filter { it.wrapperType == Track.WrapperType.AUDIOBOOK.value }
                val tracks = searchedTracks.filter { it.wrapperType == Track.WrapperType.TRACK.value }

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

                    audioBooks.forEach {
                        bindableTrackNormal {
                            id(it.trackId)
                            imageUrl(it.artworkUrl100)
                            trackTitle(it.collectionName)
                            price("${it.collectionPrice} ${it.currency}")
                            trackGenre(it.primaryGenreName)
                            onClick { _ ->
                                viewModel.viewDetails(it)
                                navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
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
                        bindableHeaderViewMore {
                            id("featureMovieHeader")
                            headerText(getString(R.string.feature_movie))
                            showViewMore(false)
                            onClick { _ -> }
                        }

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
                                id(it.trackId)
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }

                    // kind: song
                    if (song.isNotEmpty()) {
                        bindableHeaderViewMore {
                            id("songHeader")
                            headerText(getString(R.string.song))
                            showViewMore(false)
                            onClick { _ -> }
                        }

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
                                id(it.trackId)
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }

                    // kind: tv-episode
                    if (tvEpisode.isNotEmpty()) {
                        bindableHeaderViewMore {
                            id("tvEpisodeHeader")
                            headerText(getString(R.string.tv_episode))
                            showViewMore(false)
                            onClick { _ -> }
                        }

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
                                id(it.trackId)
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }

                    // kind: podcast
                    if (podcasts.isNotEmpty()) {
                        bindableHeaderViewMore {
                            id("podcastHeader")
                            headerText(getString(R.string.podcast))
                            showViewMore(false)
                            onClick { _ ->
                                viewModel.setSearchedTracks(podcasts)
                                viewModel.setNavigateFromHome(false)
                                navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
                            }
                        }

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
                                id(it.trackId)
                                imageUrl(it.artworkUrl100)
                                trackTitle(title)
                                price(price)
                                trackGenre(it.primaryGenreName)
                                onClick { _ ->
                                    viewModel.viewDetails(it)
                                    navigateTo(R.id.action_showMoreTrackFragment_to_trackDetailFragment)
                                }
                            }
                        }
                    }
                }
            } else {
                bindableEmptyScreen {
                    id("searchTracksEmptyScreen")
                    text(getString(R.string.tracks_not_found))
                    buttonVisible(false)
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

        viewModel.run {
            selectSubscribe(TrackState::searchedTracks) {
                it.takeIf { it.isNotEmpty() }?.let { list ->
                    sharedPreferences.saveViewMoreTracks(list)
                }
            }

            selectSubscribe(TrackState::navigatedFromHome) {
                sharedPreferences.saveNavigateFromHome(it)
            }
        }

        binding.run {
            toolbarMain.toolbar.run {
                title = "${getString(R.string.show_more)} Items"
                setNavigationIcon(R.drawable.ic_arrow_back_white)
                setNavigationOnClickListener { findNavController().popBackStack() }
            }

            with(epoxyRecyclerView) {
                val layoutManager = LinearLayoutManager(context)
                setLayoutManager(layoutManager)
            }
        }
    }
}
