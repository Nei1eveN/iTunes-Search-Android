package com.appetiserapps.itunessearch.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.appetiserapps.itunessearch.data.model.Track
import com.appetiserapps.itunessearch.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*---------------------------------SharedPreferences----------------------------------------------*/
/**
 * get SharedPreferences for LAST_PAGE_SHARED_PREF
 * */
fun Activity.getLastPageSharedPreferences() : SharedPreferences {
    return this.getSharedPreferences(Constants.LAST_PAGE_SHARED_PREF, Context.MODE_PRIVATE)
}

/**
 * retrieves last page id navigated saved from [SharedPreferences]
 * */
fun SharedPreferences.getLastPageId() : Int {
    return this.getInt(Constants.LAST_NAVIGATED_PAGE, 0)
}

/**
 * saves navigationController's current destination id to [SharedPreferences]
 * @param pageId id of the fragment
 * */
fun SharedPreferences?.saveLastPageId(pageId: Int) {
    this?.let { sharedPref ->
        sharedPref.edit().let { editor ->
            editor.putInt(Constants.LAST_NAVIGATED_PAGE, pageId)
            editor.apply()
        }
    }
}

/**
 * saves Boolean value for navigating condition in ShowMoreTrackFragment
 * @param navigateFromHome true if from TrackListFragment, else false
 * */
fun SharedPreferences?.saveNavigateFromHome(navigateFromHome: Boolean) {
    this?.let { sharedPref ->
        sharedPref.edit().let { editor ->
            editor.putBoolean(Constants.NAVIGATED_FROM_HOME, navigateFromHome)
            editor.apply()
        }
    }
}

/**
 * retrieves Boolean value of NAVIGATED_FROM_HOME from [SharedPreferences]
 * */
fun SharedPreferences.getNavigateFromHome() : Boolean {
    return this.getBoolean(Constants.NAVIGATED_FROM_HOME, false)
}

/**
 * save list of tracks from tapping 'MORE' button
 * @param viewMoreTracks list from the 'MORE' button of the category
 * */
fun SharedPreferences?.saveViewMoreTracks(viewMoreTracks: List<Track>) {
    this?.let { sharedPref ->
        val gson = Gson()
        val json = gson.toJson(viewMoreTracks)
        sharedPref.edit().let { editor ->
            editor.putString(Constants.LAST_VIEW_MORE_LIST, json)
            editor.apply()
        }
    }
}

/**
 * retrieves json string of viewMoreTracks from [SharedPreferences]
 * */
fun SharedPreferences.getViewMoreTracks() : String? {
    return this.getString(Constants.LAST_VIEW_MORE_LIST, "")
}

/**
 * retrieves list from json string of LAST_VIEW_MORE_LIST
 * */
fun String.getViewMoreList(): List<Track> {
    val gson = Gson()
    val lastViewedMoreTracks = arrayListOf<Track>()

    val type = object : TypeToken<List<Track>>() {}.type
    lastViewedMoreTracks.addAll(gson.fromJson(this, type))

    return lastViewedMoreTracks
}

/**
 * save list of tracks from search result from the API
 * @param searchedTracks list of tracks result from the API
 * */
fun SharedPreferences?.saveSearchResultTracks(searchedTracks: List<Track>) {
    val gson = Gson()
    val json = gson.toJson(searchedTracks)

    this?.let { sharedPref ->
        sharedPref.edit().let { editor ->
            editor.putString(Constants.LAST_RESULT_FROM_SEARCH, json)
            editor.apply()
        }
    }
}

/**
 * retrieves JSON string of LAST_RESULT_FROM_SEARCH from [SharedPreferences]
 * */
fun SharedPreferences.getSearchResultJSONString() : String? {
    return this.getString(Constants.LAST_RESULT_FROM_SEARCH, "")
}

/**
 * converts lastSearchedList json string from SharedPreferences to list
 *
 * note: this can be converted into a dynamic type of usage,
 * but it is up to your purpose
 * */
fun String.getSearchResultList() : List<Track> {
    val gson = Gson()
    val lastSearchList = arrayListOf<Track>()

    val type = object : TypeToken<List<Track>>() {}.type
    lastSearchList.addAll(gson.fromJson(this, type))
    return lastSearchList
}

/**
 * save last search keyword to SharedPreferences
 * @param searchKeyword user-input characters
 * */
fun SharedPreferences?.saveLastSearchKeyword(searchKeyword: String) {
    this?.let { sharedPref ->
        sharedPref.edit().let { editor ->
            editor.putString(Constants.LAST_SEARCH_KEYWORD, searchKeyword)
            editor.apply()
        }
    }
}

/**
 * retrieves last searched keyword from [SharedPreferences]
 * */
fun SharedPreferences.getLastSearchKeyword() : String? {
    return this.getString(Constants.LAST_SEARCH_KEYWORD, "")
}

/**
 * save trackId of the last item viewed to [SharedPreferences]
 * */
fun SharedPreferences?.saveLastTrackId(trackId: Int) {
    this?.let { sharedPref ->
        sharedPref.edit().let { editor ->
            editor.putInt(Constants.TRACK_ID, trackId)
            editor.apply()
        }
    }
}

/**
 * retrieves last track id of the item visited saved from SharedPreferences
 * */
fun SharedPreferences.getLastTrackId() : Int {
    return this.getInt(Constants.TRACK_ID, 0)
}

/*---------------------------------SharedPreferences----------------------------------------------*/