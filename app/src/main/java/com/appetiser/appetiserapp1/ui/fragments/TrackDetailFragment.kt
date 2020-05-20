package com.appetiser.appetiserapp1.ui.fragments

import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.EpoxyRecyclerView
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.databinding.FragmentTrackDetailBinding
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController

class TrackDetailFragment : EpoxyFragment<FragmentTrackDetailBinding>() {

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override val layoutId: Int
        get() = R.layout.fragment_track_detail

    override fun epoxyController() = simpleController {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
