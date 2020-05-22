package com.appetiser.appetiserapp1.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.args
import com.airbnb.mvrx.withState
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.databinding.FragmentTrackDetailBinding
import com.appetiser.appetiserapp1.ui.activities.MainActivityVM
import com.appetiser.appetiserapp1.ui.activities.TrackDetailArgs
import com.appetiser.appetiserapp1.utils.Constants
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController

class TrackDetailFragment : EpoxyFragment<FragmentTrackDetailBinding>() {

    private val viewModel: MainActivityVM by activityViewModel()

    private val args: TrackDetailArgs by args()

    override val layoutId: Int
        get() = R.layout.fragment_track_detail

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override fun epoxyController() = simpleController(viewModel) { state ->

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        withState(viewModel) { state ->
            state.item?.let { track ->
                val sharedPreferences = activity?.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
                sharedPreferences?.let { sharedPref ->
                    sharedPref.edit().let { editor ->
                        editor.putInt(Constants.TRACK_ID, track.trackId)
                        editor.apply()
                    }
                }
            } ?: run {
                val sharedPreferences = activity?.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
                sharedPreferences?.let { sharedPref ->
                    sharedPref.edit().let { editor ->
                        editor.putInt(Constants.TRACK_ID, args.trackId)
                        editor.apply()
                    }
                }
            }
        }
    }
}
