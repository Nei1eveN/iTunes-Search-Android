package com.appetiserapps.itunessearch.ui.fragments

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
import com.appetiserapps.itunessearch.bindableDescriptionHorizontal
import com.appetiserapps.itunessearch.bindableDetailAudio
import com.appetiserapps.itunessearch.bindableDetailVideo
import com.appetiserapps.itunessearch.bindableHeaderViewMore
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.databinding.FragmentTrackDetailBinding
import com.appetiserapps.itunessearch.extensions.getLastPageSharedPreferences
import com.appetiserapps.itunessearch.extensions.getLastTrackId
import com.appetiserapps.itunessearch.extensions.saveLastPageId
import com.appetiserapps.itunessearch.extensions.saveLastTrackId
import com.appetiserapps.itunessearch.extensions.toHtml
import com.appetiserapps.itunessearch.extensions.toTrackLength
import com.appetiserapps.itunessearch.ui.activities.MainActivity
import com.appetiserapps.itunessearch.ui.activities.MainActivityVM
import com.appetiserapps.itunessearch.ui.activities.TrackDetailArgs
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.nei1even.adrcodingchallengelibrary.core.binding.EpoxyFragment
import com.nei1even.adrcodingchallengelibrary.core.mvrx.simpleController

class TrackDetailFragment : EpoxyFragment<FragmentTrackDetailBinding>() {

    private val viewModel: MainActivityVM by activityViewModel()

    private val args: TrackDetailArgs by args()

    override val layoutId: Int
        get() = R.layout.fragment_track_detail

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    var player: ExoPlayer? = null

    override fun epoxyController() = simpleController(viewModel) { state ->
        state.item?.let { track ->

            binding.run {
                appBarLayout.imageUrl = track.artworkUrl100
                val toolbar = appBarLayout.toolbar
                toolbar.run {
                    setNavigationIcon(R.drawable.ic_arrow_back_white)
                    setNavigationOnClickListener { findNavController().popBackStack() }
                }
            }

            when(track.wrapperType) {
                Track.WrapperType.AUDIOBOOK.value -> {
                    bindableDetailAudio {
                        id("previewUrl${track.collectionId}")
                        previewUrl(track.previewUrl)
                        player(player)
                    }
                    // title: collection
                    bindableHeaderViewMore {
                        id("title${track.collectionId}")
                        headerText(
                            when {
                                track.collectionName.isNotEmpty() -> track.collectionName
                                else -> track.collectionCensoredName
                            }
                        )
                        showViewMore(false)
                    }
                    // genre
                    bindableDescriptionHorizontal {
                        id("genre${track.collectionId}")
                        headerText(getString(R.string.genre))
                        detailsText(track.primaryGenreName)
                    }
                    // country / from
                    bindableDescriptionHorizontal {
                        id("country${track.collectionId}")
                        headerText(getString(R.string.country))
                        detailsText(track.country)
                    }
                    // price
                    bindableDescriptionHorizontal {
                        id("price${track.collectionId}")
                        headerText(getString(R.string.price))
                        detailsText("${track.collectionPrice} ${track.currency}")
                    }
                    // collection description
                    bindableDescription {
                        id("description${track.collectionId}")
                        showHeader(false)
                        detailsText(track.description.toHtml())
                    }
                    // collection artist / author
                    bindableDescription {
                        id("artistName${track.collectionId}")
                        showHeader(true)
                        headerText(getString(R.string.author))
                        detailsText(track.artistName.toHtml())
                    }
                    // copyright
                    when {
                        track.copyright.isNotEmpty() -> {
                            bindableDescription {
                                id("copyright${track.collectionId}")
                                showHeader(true)
                                headerText(getString(R.string.copyright))
                                detailsText(track.copyright.toHtml())
                            }
                        }
                    }
                }
                else -> {
                    when (track.kind) {
                        Track.TrackKind.FEATURE_MOVIE.value, Track.TrackKind.TV_EPISODE.value -> {
                            bindableDetailVideo {
                                id("previewUrl${track.trackId}")
                                previewUrl(track.previewUrl)
                                player(player)
                            }
                        }
                        Track.TrackKind.SONG.value -> {
                            bindableDetailAudio {
                                id("previewUrl${track.trackId}")
                                previewUrl(track.previewUrl)
                                player(player)
                            }
                        }
                    }
                    // title: track
                    bindableHeaderViewMore {
                        id("title${track.trackId}")
                        headerText(
                            when {
                                track.trackName.isNotEmpty() -> track.trackName
                                else -> track.trackCensoredName
                            }
                        )
                        showViewMore(false)
                    }
                    when (track.kind) {
                        Track.TrackKind.FEATURE_MOVIE.value, Track.TrackKind.TV_EPISODE.value -> {
                            // track collection name
                            bindableDescriptionHorizontal {
                                id("collection${track.trackId}")
                                headerText(getString(R.string.from)+" ${getString(R.string.collection)}")
                                detailsText(
                                    when {
                                        track.collectionName.isNotEmpty() -> track.collectionName
                                        else -> track.collectionCensoredName
                                    }
                                )
                            }
                        }
                        Track.TrackKind.SONG.value -> {
                            // artist
                            bindableDescriptionHorizontal {
                                id("artistName${track.trackId}")
                                headerText(getString(R.string.artist))
                                detailsText(track.artistName)
                            }
                            // song duration / length
                            bindableDescriptionHorizontal {
                                id("trackInMillis${track.trackId}")
                                headerText(getString(R.string.track_duration))
                                detailsText(track.trackTimeMillis.toTrackLength())
                            }
                            // track number
                            bindableDescriptionHorizontal {
                                id("trackNumber${track.trackId}")
                                headerText(getString(R.string.track_no))
                                detailsText(track.trackNumber.toString())
                            }
                            // track collection name
                            bindableDescriptionHorizontal {
                                id("collection${track.trackId}")
                                headerText(getString(R.string.album))
                                detailsText(
                                    when {
                                        track.collectionName.isNotEmpty() -> track.collectionName
                                        else -> track.collectionCensoredName
                                    }
                                )
                            }
                        }
                    }
                    // genre
                    bindableDescriptionHorizontal {
                        id("genre${track.trackId}")
                        headerText(getString(R.string.genre))
                        detailsText(track.primaryGenreName)
                    }
                    // country / from
                    bindableDescriptionHorizontal {
                        id("country${track.trackId}")
                        headerText(getString(R.string.country))
                        detailsText(track.country)
                    }
                    // price
                    bindableDescriptionHorizontal {
                        id("price${track.trackId}")
                        headerText(getString(R.string.price))
                        detailsText("${when (track.kind) {
                            Track.TrackKind.FEATURE_MOVIE.value, Track.TrackKind.TV_EPISODE.value -> track.collectionPrice
                            else -> track.trackPrice
                        }} ${track.currency}")
                    }
                    // track description / directed by / artist
                    when (track.kind) {
                        Track.TrackKind.FEATURE_MOVIE.value, Track.TrackKind.TV_EPISODE.value -> {
                            // track description
                            bindableDescription {
                                id("description${track.trackId}")
                                showHeader(false)
                                detailsText(track.longDescription.toHtml())
                            }
                            // directed by
                            bindableDescription {
                                id("artistName${track.trackId}")
                                showHeader(true)
                                headerText(getString(R.string.directed_by))
                                detailsText(track.artistName.toHtml())
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

        player = context?.let { SimpleExoPlayer.Builder(it).build() }
        (player as SimpleExoPlayer?)?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING

        binding.run {
            with(epoxyRecyclerView) {
                val layoutManager = LinearLayoutManager(context)
                setLayoutManager(layoutManager)
            }
        }
        val sharedPreferences = activity?.getLastPageSharedPreferences()
        findNavController().currentDestination?.id?.let { sharedPreferences.saveLastPageId(it) }
        withState(viewModel) { state ->
            state.item?.let { track ->
                sharedPreferences.saveLastTrackId(track.trackId)
            } ?: run {
                val trackId = sharedPreferences?.getLastTrackId() ?: 0
                viewModel.viewDetails(args.trackId ?: trackId)
                sharedPreferences.saveLastTrackId(args.trackId ?: trackId)
            }
        }
    }

    /**
     * releases the player when Lifecycle is onPause
     * */
    override fun onPause() {
        super.onPause()
        player?.release()
    }
}
