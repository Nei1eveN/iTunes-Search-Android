package com.appetiser.appetiserapp1.ui.fragments

import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.core.EpoxyFragment
import com.appetiser.appetiserapp1.core.simpleController
import com.appetiser.appetiserapp1.databinding.FragmentTrackSearchBinding

/**
 * A simple [Fragment] subclass.
 */
class TrackSearchFragment : EpoxyFragment<FragmentTrackSearchBinding>() {

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override val layoutId: Int
        get() = R.layout.fragment_track_search

    override fun epoxyController() = simpleController {

    }
}
