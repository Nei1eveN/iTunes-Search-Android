package com.appetiser.appetiserapp1.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.appetiser.appetiserapp1.BindableTrackBindingModel_
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.bindableHeaderViewMore
import com.appetiser.appetiserapp1.core.activity.BindingEpoxyActivity
import com.appetiser.appetiserapp1.core.simpleController
import com.appetiser.appetiserapp1.data.Track
import com.appetiser.appetiserapp1.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.app_bar_main.view.toolbar

class MainActivity : BindingEpoxyActivity<ActivityMainBinding>() {

    override val recyclerView: EpoxyRecyclerView
        get() = binding.previousVisitedRecyclerView

    private val toggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbarMain.toolbar,
            0,
            0
        )
    }

    override fun epoxyController() = simpleController {
        bindableHeaderViewMore {
            id("previouslyVisited")
            headerText(getString(R.string.previously_visited))
            showViewMore(false)
            onClick { _ ->
                Toast.makeText(this@MainActivity, getString(R.string.previously_visited), Toast.LENGTH_SHORT).show()
            }
        }

        val models = arrayListOf<Track>()
        models.add(Track(trackId = 1437031362, trackName = "A Star Is Born (2018)", artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Video128/v4/75/6e/46/756e4680-9493-f58c-3d3f-947d36e5c32a/source/360x360bb.jpg"))
        models.add(Track(trackId = 1063466898, trackName = "Star Wars: The Force Awakens", artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Video123/v4/1f/2b/ae/1f2bae7f-62a1-1055-8471-401291b6dcdd/pr_source.lsr/360x360bb.jpg"))
        models.add(Track(trackId = 1490359713, trackName = "Star Wars: The Rise of Skywalker", artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Video123/v4/dc/70/0f/dc700f1d-4566-a487-7585-8a8176ab38fd/pr_source.jpg/360x360bb.jpg"))
        models.add(Track(trackId = 976965981, trackName = "Star Wars: Return of the Jedi", artworkUrl100 = "https://is3-ssl.mzstatic.com/image/thumb/Video123/v4/9f/f9/e8/9ff9e804-f7f5-2a01-54a8-c476a5a884fa/pr_source.lsr/360x360bb.jpg"))

        val tracks = models.map {
            BindableTrackBindingModel_()
                .id(it.trackId)
                .imageUrl(it.artworkUrl100)
                .trackTitle(it.trackName)
        }

        carousel {
            id("previousVisitedCarousel")
            /*paddingRes(R.dimen.view_pager_item_padding)*/
            padding(Carousel.Padding.dp(0,4,0,16,4))
            hasFixedSize(true)
            models(tracks)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind(R.layout.activity_main)

        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        binding.run {
            setSupportActionBar(toolbarMain.toolbar)

            with(drawerLayout) {
                toggle.syncState()
            }
        }
    }
}
