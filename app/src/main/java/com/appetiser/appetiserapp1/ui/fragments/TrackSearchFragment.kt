package com.appetiser.appetiserapp1.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.bindableEmptyScreen
import com.appetiser.appetiserapp1.core.EpoxyFragment
import com.appetiser.appetiserapp1.core.simpleController
import com.appetiser.appetiserapp1.databinding.FragmentTrackSearchBinding
import com.appetiser.appetiserapp1.extensions.hideKeyboard
import com.appetiser.appetiserapp1.extensions.makeSafeSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class TrackSearchFragment : EpoxyFragment<FragmentTrackSearchBinding>() {

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override val layoutId: Int
        get() = R.layout.fragment_track_search

    private val viewModel: TrackSearchFragmentVM by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->
        if (state.searchText.isEmpty()) {
            bindableEmptyScreen {
                id("emptyScreen")
                text(getString(R.string.search_track))
                buttonVisible(false)
                onClick { _ -> }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            val searchText = binding.searchToolbar.searchText
            val countryDropdown = binding.searchToolbar.countryText

            searchText.run {

                withState(viewModel) {
                    setText(it.searchText)

                    countryDropdown.run {
                        it.country.takeIf { value -> value.isNotEmpty() }?.let { value ->
                            setAdapter(ArrayAdapter(this.context, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.preset_country_codes)))
                            setText(value)
                        } ?: kotlin.run {
                            setAdapter(ArrayAdapter(this.context, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.preset_country_codes)))
                        }
                    }

                    setOnEditorActionListener { _, actionId, _ ->
                        when(actionId) {
                            EditorInfo.IME_ACTION_SEARCH -> {
                                when {
                                    this.text.toString().isEmpty() -> {
                                        makeSafeSnackbar("Please enter a text", Snackbar.LENGTH_LONG)?.show()
                                    }
                                    countryDropdown.text.toString().isEmpty() -> {
                                        makeSafeSnackbar("Please select a country", Snackbar.LENGTH_LONG)?.show()
                                    }
                                    else -> {
                                        activity?.hideKeyboard()
                                        viewModel.searchTrack(this.text.toString(), countryDropdown.text.toString())
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    }
                }


            }
        }
    }
}
