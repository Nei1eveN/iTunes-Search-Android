package com.appetiser.appetiserapp1.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * handles keyboard visibility and hide to current focus or view
 */
fun Activity.hideKeyboard() {
    this.currentFocus?.let { focus ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(focus.windowToken, 0)
    }
}