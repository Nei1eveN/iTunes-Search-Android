package com.appetiser.appetiserapp1.core

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.appetiser.appetiserapp1.core.activity.BindingMvRxActivity

class MvRxEpoxyController(
    val buildModelsCallback: EpoxyController.() -> Unit = {}
) : AsyncEpoxyController() {

    override fun buildModels() {
        buildModelsCallback()
    }
}

fun BindingMvRxActivity<*>.simpleController(
    buildModels: EpoxyController.() -> Unit
) = MvRxEpoxyController{
    if (isFinishing) return@MvRxEpoxyController
    buildModels()
}


fun <S : MvRxState, A : BaseMvRxViewModel<S>> BindingMvRxActivity<*>.simpleController(
    viewModel: A,
    buildModels: EpoxyController.(state: S) -> Unit
) = MvRxEpoxyController {
    if (isFinishing) return@MvRxEpoxyController
    com.airbnb.mvrx.withState(viewModel) { state ->
        buildModels(state)
    }
}