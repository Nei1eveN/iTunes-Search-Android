package com.appetiserapps.itunessearch.extensions

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.airbnb.mvrx.MvRx
import com.appetiserapps.itunessearch.R

/**
 * Navigate function extension for navigation controller inside the Activity
 * */
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

@Suppress("DEPRECATION")
fun String.toHtml() : Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}