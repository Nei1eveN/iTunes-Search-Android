package com.appetiserapps.itunessearch.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.activityViewModel
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.bindableEmptyScreen
import com.appetiserapps.itunessearch.bindableHeaderViewMore
import com.appetiserapps.itunessearch.bindableTrackNormal
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.databinding.FragmentTrackListBinding
import com.appetiserapps.itunessearch.extensions.getLastPageSharedPreferences
import com.appetiserapps.itunessearch.extensions.saveLastPageId
import com.appetiserapps.itunessearch.extensions.toDateFormat
import com.appetiserapps.itunessearch.ui.activities.MainActivity
import com.appetiserapps.itunessearch.ui.activities.MainActivityVM
import com.appetiserapps.itunessearch.ui.activities.TrackState
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController
import kotlinx.android.synthetic.main.toolbar_main.view.toolbar
import java.util.Date

/**
 * A simple [Fragment] subclass.
 */
class TrackListFragment : EpoxyFragment<FragmentTrackListBinding>() {

    private val viewModel: MainActivityVM by activityViewModel()

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override val layoutId: Int
        get() = R.layout.fragment_track_list

    override fun epoxyController() = simpleController(viewModel) { state ->
        if (state.tracks.isEmpty()) {
            bindableEmptyScreen {
                id("emptyScreen")
                text(getString(R.string.tracks_not_found))
                buttonVisible(true)
                onClick { _ ->
                    navigateTo(R.id.trackSearchFragment)
                }
            }
        } else {
            val previouslyVisitedTracks = state.tracks.filter { it.previouslyVisited }
            val visitedTracksToday = previouslyVisitedTracks.filter { it.date.toDateFormat() == Date().toDateFormat() }
            val atLeastOneVisited = previouslyVisitedTracks.isNotEmpty()
            val moreThanThree = previouslyVisitedTracks.size > 3
            if (atLeastOneVisited) {
                bindableHeaderViewMore {
                    id("previouslyVisited")
                    headerText(getString(R.string.previously_visited))
                    showViewMore(moreThanThree)
                    onClick { _ ->
                        viewModel.setNavigateFromHome(true)
                        navigateTo(R.id.action_trackListFragment_to_showMoreTrackFragment)
                    }
                }

                if (visitedTracksToday.isNotEmpty()) {
                    bindableHeaderViewMore {
                        id("visitedToday")
                        headerText(getString(R.string.visited_today))
                        showViewMore(false)
                        onClick { _ -> }
                    }
                    visitedTracksToday.forEach {
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
                                navigateTo(R.id.action_trackListFragment_to_trackDetailFragment)
                            }
                        }
                    }
                } else {
                    bindableEmptyScreen {
                        id("visitedTracksTodayEmptyScreen")
                        text(getString(R.string.no_tracks_today))
                        buttonVisible(false)
                        onClick { _ ->
                            viewModel.setNavigateFromHome(true)
                            navigateTo(R.id.action_trackListFragment_to_showMoreTrackFragment)
                        }
                    }
                }




            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getLastPageSharedPreferences()
        findNavController().currentDestination?.id?.let { sharedPreferences.saveLastPageId(it) }

        val addButton = (activity as MainActivity).binding.addButton
        viewModel.selectSubscribe(TrackState::tracks) {
            if (it.isNotEmpty()) addButton.show()
            else addButton.hide()
        }

        binding.run {
            toolbarMain.toolbar.title = "Home"

            with(epoxyRecyclerView) {
                val layoutManager = LinearLayoutManager(context)
                setLayoutManager(layoutManager)
            }
        }

    }
}
