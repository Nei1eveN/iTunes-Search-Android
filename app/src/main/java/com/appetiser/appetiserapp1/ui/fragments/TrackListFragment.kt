package com.appetiser.appetiserapp1.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.activityViewModel
import com.appetiser.appetiserapp1.BindableTrackGridBindingModel_
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.bindableEmptyScreen
import com.appetiser.appetiserapp1.bindableHeaderViewMore
import com.appetiser.appetiserapp1.databinding.FragmentTrackListBinding
import com.appetiser.appetiserapp1.ui.activities.MainActivity
import com.appetiser.appetiserapp1.ui.activities.MainActivityVM
import com.appetiser.appetiserapp1.ui.activities.TrackState
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController
import kotlinx.android.synthetic.main.toolbar_main.view.toolbar

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
            val atLeastOneVisited = previouslyVisitedTracks.isNotEmpty()
            val moreThanThree = previouslyVisitedTracks.size > 3
            if (atLeastOneVisited) {
                bindableHeaderViewMore {
                    id("previouslyVisited")
                    headerText(getString(R.string.previously_visited))
                    showViewMore(moreThanThree)
                    onClick { view ->
                        Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
                    }
                }

                carousel {
                    id("previousVisitedCarousel")
                    padding(Carousel.Padding.dp(0,4,0,16,1))
                    hasFixedSize(true)
                    previouslyVisitedTracks.map {
                        BindableTrackGridBindingModel_()
                            .id(it.trackId)
                            .imageUrl(it.artworkUrl100)
                            .trackTitle(it.trackName)
                            .trackPrice(it.trackPrice.toString())
                            .trackGenre(it.primaryGenreName)
                            .onClick { _ ->
                                viewModel.viewDetails(it)
                                navigateTo(R.id.action_trackListFragment_to_trackDetailFragment)
                            }
                    }.let { models(it) }
                    numViewsToShowOnScreen(if (atLeastOneVisited) 1f else 0f)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = (activity as MainActivity).binding.addButton
        viewModel.run {
            selectSubscribe(TrackState::tracks) {
                when {
                    it.isEmpty() -> addButton.hide()
                    else -> addButton.show()
                }
            }
        }

        binding.run {
            toolbarMain.toolbar.title = "Home"
        }

    }
}
