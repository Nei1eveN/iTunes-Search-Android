package com.appetiserapps.itunessearch.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.args
import com.airbnb.mvrx.withState
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.bindableDescription
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.databinding.FragmentTrackDetailBinding
import com.appetiserapps.itunessearch.extensions.toHtml
import com.appetiserapps.itunessearch.ui.activities.MainActivityVM
import com.appetiserapps.itunessearch.ui.activities.TrackDetailArgs
import com.appetiserapps.itunessearch.utils.Constants
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController
import kotlinx.android.synthetic.main.app_bar_main.toolbar

class TrackDetailFragment : EpoxyFragment<FragmentTrackDetailBinding>() {

    private val viewModel: MainActivityVM by activityViewModel()

    private val args: TrackDetailArgs by args()

    override val layoutId: Int
        get() = R.layout.fragment_track_detail

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override fun epoxyController() = simpleController(viewModel) { state ->
        state.item?.let { track ->

            binding.run {
                appBarLayout.imageUrl = track.artworkUrl100
                val toolbar = appBarLayout.toolbar
                toolbar.run {
                    setNavigationIcon(R.drawable.ic_arrow_back_white)
                    setNavigationOnClickListener { findNavController().popBackStack() }
                    subtitle = track.artistName
                }
            }

            when(track.wrapperType) {
                Track.WrapperType.AUDIOBOOK.value -> {
                    toolbar.title = when {
                        track.collectionName.isNotEmpty() -> track.collectionName
                        else -> track.collectionCensoredName
                    }
                    bindableDescription {
                        id("description")
                        showHeader(true)
                        headerText("Description")
                        detailsText(track.description.toHtml())
                    }
                }
                else -> {
                    toolbar.title = when {
                        track.trackName.isNotEmpty() -> track.trackName
                        else -> track.trackCensoredName
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            with(epoxyRecyclerView) {
                val layoutManager = LinearLayoutManager(context)
                setLayoutManager(layoutManager)
            }
        }

        val sharedPreferences = activity?.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
        withState(viewModel) { state ->
            state.item?.let { track ->
                sharedPreferences?.let { sharedPref ->
                    sharedPref.edit().let { editor ->
                        editor.putInt(Constants.TRACK_ID, track.trackId)
                        findNavController().currentDestination?.id?.let { editor.putInt(Constants.LAST_NAVIGATED_PAGE, it) }
                        editor.apply()
                    }
                }
            } ?: run {
                sharedPreferences?.let { sharedPref ->
                    sharedPref.edit().let { editor ->
                        editor.putInt(Constants.TRACK_ID, args.trackId)
                        findNavController().currentDestination?.id?.let { editor.putInt(Constants.LAST_NAVIGATED_PAGE, it) }
                        editor.apply()
                        viewModel.viewDetails(args.trackId)
                    }
                }
            }
        }
    }
}
