package com.appetiser.appetiserapp1.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

fun View.makeSafeSnackbar(text: CharSequence, duration: Int): Snackbar? {
    fun findSuitableParent(view: View): ViewGroup? {
        var temporaryView: View? = view
        var fallback: ViewGroup? = null

        do {
            if (temporaryView is CoordinatorLayout) {
                return temporaryView
            }
            if (temporaryView is FrameLayout) {
                if (temporaryView.id == 16908290) {
                    return temporaryView
                }
                fallback = temporaryView
            }
            if (temporaryView != null) {
                val parent = temporaryView.parent
                temporaryView = if (parent is View) parent else null
            }
        } while (temporaryView != null)
        return fallback
    }

    val parent = findSuitableParent(this)

    return if (parent != null) {
        Snackbar.make(this, text, duration)
    } else null
}