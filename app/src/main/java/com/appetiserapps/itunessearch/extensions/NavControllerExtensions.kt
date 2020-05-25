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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

/**
 * converts string to [Spanned] value
 * */
@Suppress("DEPRECATION")
fun String.toHtml() : Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

/**
 * converts track.trackTimeMillis into minutes and seconds
 * */
fun Int.toTrackLength() : String {
    val seconds = (this / 1000) % 60
    val minutes = ((this - seconds) / 1000) / 60

    return "$minutes:$seconds"
}

/**
 * converts default date format saved when user selected an item to Month, Day, and Year
 * */
fun Date.toDateFormat() : String {
    val oldDateFormat = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.getDefault())
    val thisDate = oldDateFormat.parse(this.toString()) ?: Date()
    val newDateFormat = "MMM dd yyyy"
    oldDateFormat.applyPattern(newDateFormat)
    return oldDateFormat.format(thisDate)
}
