package com.appetiser.appetiserapp1.ui.fragments

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.appetiser.appetiserapp1.BindableTrackGridBindingModel_
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.bindableHeaderViewMore
import com.appetiser.appetiserapp1.core.EpoxyFragment
import com.appetiser.appetiserapp1.core.simpleController
import com.appetiser.appetiserapp1.data.Track
import com.appetiser.appetiserapp1.databinding.FragmentTrackListBinding

/**
 * A simple [Fragment] subclass.
 */
class TrackListFragment : EpoxyFragment<FragmentTrackListBinding>() {

    override val recyclerView: EpoxyRecyclerView
        get() = binding.epoxyRecyclerView

    override val layoutId: Int
        get() = R.layout.fragment_track_list

    override fun epoxyController() = simpleController {
        val models = arrayListOf<Track>()
        models.add(Track(trackId = 1437031362, trackName = "A Star Is Born (2018)", artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Video128/v4/75/6e/46/756e4680-9493-f58c-3d3f-947d36e5c32a/source/360x360bb.jpg"))
        /*models.add(Track(trackId = 1063466898, trackName = "Star Wars: The Force Awakens", artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Video123/v4/1f/2b/ae/1f2bae7f-62a1-1055-8471-401291b6dcdd/pr_source.lsr/360x360bb.jpg"))
        models.add(Track(trackId = 1490359713, trackName = "Star Wars: The Rise of Skywalker", artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Video123/v4/dc/70/0f/dc700f1d-4566-a487-7585-8a8176ab38fd/pr_source.jpg/360x360bb.jpg"))
        models.add(Track(trackId = 976965981, trackName = "Star Wars: Return of the Jedi", artworkUrl100 = "https://is3-ssl.mzstatic.com/image/thumb/Video123/v4/9f/f9/e8/9ff9e804-f7f5-2a01-54a8-c476a5a884fa/pr_source.lsr/360x360bb.jpg"))*/

        bindableHeaderViewMore {
            id("previouslyVisited")
            headerText(getString(R.string.previously_visited))
            showViewMore(models.size > 1)
            onClick { view ->
                Toast.makeText(view.context, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
            }
        }

        val tracks = models.map {
            BindableTrackGridBindingModel_()
                .id(it.trackId)
                .imageUrl(it.artworkUrl100)
                .trackTitle(it.trackName)
                .onClick { _ -> }
        }

        carousel {
            id("previousVisitedCarousel")
            padding(Carousel.Padding.dp(0,4,0,16,1))
            hasFixedSize(true)
            models(tracks)
            numViewsToShowOnScreen(if (models.size > 1) 1f else 0f)
        }
    }
}
