package com.appetiserapps.itunessearch.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class NonSwipeableRefreshLayout : SwipeRefreshLayout {

    constructor(context: Context) : super(context) {
        isEnabled = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        isEnabled = false
    }

}