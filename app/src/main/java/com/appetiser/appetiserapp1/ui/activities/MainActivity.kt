package com.appetiser.appetiserapp1.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.navigation.findNavController
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.core.extensions.navigateTo
import com.appetiser.appetiserapp1.databinding.ActivityMainBinding
import com.appetiser.appetiserapp1.utils.Constants
import com.nei1even.adrcodingchallengelibrary.core.activity.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val navigationController by lazy { findNavController(R.id.nav_host_fragment) }

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind(R.layout.activity_main)

        val sharedPref = getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
        val trackId = sharedPref.getInt(Constants.TRACK_ID, 0)
        val lastNavigatedPageId = sharedPref.getInt(Constants.LAST_NAVIGATED_PAGE, 0 )
        when {
            trackId > 0 && lastNavigatedPageId == R.id.trackDetailFragment -> {
                navigationController.navigateTo(lastNavigatedPageId, TrackDetailArgs(trackId = trackId))
            }
            lastNavigatedPageId > 0 && lastNavigatedPageId == R.id.trackSearchFragment -> {
                navigationController.navigate(lastNavigatedPageId)
            }
        }

        binding.run {
            navigationController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.trackListFragment -> addButton.show()
                    else -> addButton.hide()
                }
                sharedPref.edit().apply {
                    putInt(Constants.LAST_NAVIGATED_PAGE, destination.id)
                    apply()
                }
            }

            setFabOnClick {
                navigationController.navigate(R.id.trackSearchFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when(navigationController.currentDestination?.id) {
            R.id.trackListFragment -> finish()
            R.id.trackDetailFragment, R.id.trackSearchFragment -> navigationController.popBackStack()
            else -> super.onBackPressed()
        }
    }
}
