package com.appetiser.appetiserapp1.core.activity

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.EpoxyRecyclerView
import com.appetiser.appetiserapp1.core.MvRxEpoxyController

abstract class BindingEpoxyActivity<B : ViewDataBinding> : BindingMvRxActivity<B>() {

    abstract val recyclerView: EpoxyRecyclerView

    protected val epoxyController by lazy { epoxyController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * Provide the EpoxyController to use when building models for this Fragment.
     * Basic usages can simply use [simpleController]
     */
    abstract fun epoxyController(): MvRxEpoxyController

    override fun bind(layoutResID: Int) {
        super.bind(layoutResID)
        recyclerView.setController(epoxyController)
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroy()
    }

}