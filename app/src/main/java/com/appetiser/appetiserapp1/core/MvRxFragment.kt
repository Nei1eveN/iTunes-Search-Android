package com.appetiser.appetiserapp1.core

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.appetiser.appetiserapp1.R

abstract class MvRxFragment<B : ViewDataBinding> : BaseMvRxFragment() {

    open lateinit var binding: B

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }


    override fun invalidate() {
    }


    protected fun navigateTo(@IdRes actionId: Int, arg: Parcelable? = null) {
        /**
         * If we put a parcelable arg in [MvRx.KEY_ARG] then MvRx will attempt to call a secondary
         * constructor on any MvRxState objects and pass in this arg directly.
         */
        val bundle = arg?.let { Bundle().apply { putParcelable(MvRx.KEY_ARG, it) } }
//        findNavController(this).navigate(actionId, bundle)
        findNavController(this).navigate(
            actionId, bundle, NavOptions.Builder()
                .setEnterAnim(R.anim.abc_fade_in)
                .setPopExitAnim(R.anim.abc_fade_out)
                .setExitAnim(R.anim.abc_fade_out).build()
        )
    }

    private var activityResultListener: ((resultCode: Int, data: Intent?) -> Unit)? = null
    private var activityResultRequestCode: Int = 0

    fun startActivityForResult(
        intent: Intent,
        requestCode: Int,
        resultListener: (resultCode: Int, data: Intent?) -> Unit
    ) {
        this.activityResultListener = resultListener
        this.activityResultRequestCode = requestCode
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (activityResultRequestCode == requestCode && activityResultListener != null) {
            activityResultListener?.invoke(resultCode, data)
        }
    }

}