package com.appetiser.appetiserapp1.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.findNavController
import com.appetiser.appetiserapp1.R
import com.appetiser.appetiserapp1.binding.BindingActivity
import com.appetiser.appetiserapp1.databinding.ActivityMainBinding

class MainActivity : BindingActivity<ActivityMainBinding>() {

    private val navigationController by lazy { findNavController(R.id.nav_host_fragment) }

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind(R.layout.activity_main)

        binding.run {
            navigationController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.trackListFragment -> addButton.show()
                    else -> addButton.hide()
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
