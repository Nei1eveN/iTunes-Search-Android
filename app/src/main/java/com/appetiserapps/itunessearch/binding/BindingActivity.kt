package com.appetiserapps.itunessearch.binding

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.appetiserapps.itunessearch.R
import com.nei1even.adrcodingchallengelibrary.core.activity.BaseActivity

abstract class BindingActivity<B : ViewDataBinding> : BaseActivity<B>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = when {
            !resources.getBoolean(R.bool.is_tablet) -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

}