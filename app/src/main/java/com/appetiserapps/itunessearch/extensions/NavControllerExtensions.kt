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
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.ui.activities.ExpandableTrackItem
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
fun String.toHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

/**
 * converts track.trackTimeMillis into minutes and seconds
 * */
fun Int.toTrackLength(): String {
    val seconds = (this / 1000) % 60
    val minutes = ((this - seconds) / 1000) / 60

    val convertedSeconds = if (seconds < 10) "0$seconds" else "$seconds"

    return "$minutes:${convertedSeconds}"
}

/**
 * converts default date format saved when user selected an item to Month, Day, and Year
 * */
fun Date.toDateFormat(): String {
    val oldDateFormat = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
    val thisDate = oldDateFormat.parse(this.toString()) ?: Date()
    val newDateFormat = "MMM dd yyyy"
    oldDateFormat.applyPattern(newDateFormat)
    return oldDateFormat.format(thisDate)
}

/**
 * converts list [Track] into an expandable list item of title and list of tracks
 *
 * note: this can be converted into a dynamic type of usage,
 * but it is up to your purpose
 * */
fun List<Track>.groupIntoTracksByDefaultDateFormat(): List<ExpandableTrackItem> {
    val groupedHashMap = mutableMapOf<String, MutableSet<Track>>()

    this.forEach {
        val hashMapKey = it.date.toDateFormat()

        if (groupedHashMap.containsKey(hashMapKey)) {
            groupedHashMap[hashMapKey]?.add(it)
        } else {
            val trackSet = mutableSetOf<Track>()
            trackSet.add(it)
            groupedHashMap[hashMapKey] = trackSet
        }
    }

    val consolidatedList = arrayListOf<ExpandableTrackItem>()
    for (date in groupedHashMap.keys) {
        val dateTrackItem = ExpandableTrackItem()
        dateTrackItem.title = date
        val tracks = mutableListOf<Track>()
        groupedHashMap[date]?.iterator()?.forEach { track ->
            tracks.add(track)
            dateTrackItem.tracks = tracks
        }
        consolidatedList.add(dateTrackItem)
    }
    return consolidatedList
}

