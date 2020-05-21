package com.appetiser.appetiserapp1.core.extensions

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.airbnb.mvrx.MvRx
import com.appetiser.appetiserapp1.R

@SuppressLint("PrivateResource")
fun NavController.navigateTo(actionId: Int, arg: Parcelable? = null) {
    val bundle = arg?.let { Bundle().apply { putParcelable(MvRx.KEY_ARG, it) } }
    navigate(
        actionId, bundle, NavOptions.Builder()
            .setEnterAnim(R.anim.abc_fade_in)
            .setPopExitAnim(R.anim.abc_fade_out)
            .setExitAnim(R.anim.abc_fade_out).build()
    )
}