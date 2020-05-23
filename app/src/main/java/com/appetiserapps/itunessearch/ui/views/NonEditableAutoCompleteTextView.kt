package com.appetiserapps.itunessearch.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class NonEditableAutoCompleteTextView : AppCompatAutoCompleteTextView {

    constructor(context: Context) : super(context) {
        keyListener = null
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        keyListener = null
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        keyListener = null
    }
}