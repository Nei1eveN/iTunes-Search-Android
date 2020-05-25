package com.appetiserapps.itunessearch.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.findNavController
import com.appetiserapps.itunessearch.R
import com.appetiserapps.itunessearch.binding.BindingActivity
import com.appetiserapps.itunessearch.databinding.ActivityMainBinding

class MainActivity : BindingActivity<ActivityMainBinding>() {

    private val navigationController by lazy { findNavController(R.id.nav_host_fragment) }

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind(R.layout.activity_main)

        binding.run {
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
