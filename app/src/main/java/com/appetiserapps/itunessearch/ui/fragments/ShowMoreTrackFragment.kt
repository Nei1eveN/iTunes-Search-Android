package com.appetiserapps.itunessearch.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.activityViewModel
import com.appetiserapps.itunessearch.BindableTrackGridBindingModel_
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.bindableHeaderViewMore
import com.appetiserapps.itunessearch.bindableTrackNormal
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.databinding.FragmentTrackListBinding
import com.appetiserapps.itunessearch.ui.activities.MainActivity
import com.appetiserapps.itunessearch.ui.activities.MainActivityVM
import com.appetiserapps.itunessearch.utils.Constants
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController

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
            val previouslyVisitedTracks = state.tracks.filter { it.previouslyVisited }.sortedByDescending { it.date }
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

                    if (moreThanThree) {
                        carousel {
                            id("audioBooksCarousel")
                            paddingRes(R.dimen.activity_horizontal_margin_default)
                            hasFixedSize(true)
                            audioBooks.map {
                                BindableTrackGridBindingModel_()
                                    .id(it.trackId)
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
                                id(it.trackId)
                                imageUrl(it.artworkUrl100)
                                trackTitle(it.collectionName)
                                price("${it.collectionPrice} ${it.currency}")
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

                    // kind: feature-movie
                    if (featureMovie.isNotEmpty()) {
                        val moreThanThree = featureMovie.size >= 3

                        bindableHeaderViewMore {
                            id("featureMovieHeader")
                            headerText(getString(R.string.feature_movie))
                            showViewMore(false)
                            onClick { _ -> }
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
                                        .id(it.trackId)
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
                                    id(it.trackId)
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
                                Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
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
                                        .id(it.trackId)
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
                                    id(it.trackId)
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
                            onClick { view ->
                                Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
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
                                        .id(it.trackId)
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
                                    id(it.trackId)
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
            } else {

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = (activity as MainActivity).binding.addButton
        addButton.hide()

        val sharedPreferences = activity?.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
        findNavController().currentDestination?.id?.let { saveLastNavigatedPage(sharedPreferences, it) }

        binding.run {
            with(epoxyRecyclerView) {
                val layoutManager = LinearLayoutManager(context)
                setLayoutManager(layoutManager)
            }
        }
    }

    private fun saveLastNavigatedPage(sharedPreferences: SharedPreferences?, pageId: Int) {
        sharedPreferences?.let { sharedPref ->
            sharedPref.edit().let { editor ->
                editor.putInt(Constants.LAST_NAVIGATED_PAGE, pageId)
                editor.apply()
            }
        }
    }
}
