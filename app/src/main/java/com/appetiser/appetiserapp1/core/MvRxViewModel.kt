package com.appetiser.appetiserapp1.core

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState

abstract class MvRxViewModel<S : MvRxState>(initialState: S, debugMode: Boolean = BuildConfig.DEBUG) :
    BaseMvRxViewModel<S>(initialState, debugMode)