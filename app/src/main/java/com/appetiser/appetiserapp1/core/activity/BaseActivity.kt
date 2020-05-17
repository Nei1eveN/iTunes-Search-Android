package com.appetiser.appetiserapp1.core.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    private var activityResultListener: ((resultCode: Int, data: Intent?) -> Unit)? = null
    private var activityResultRequestCode: Int = 0

    private var permissionGranted: (() -> Unit)? = null
    private var permissionDenied: (() -> Unit)? = null

    private var currentFragmentName = ""

    lateinit var binding: B

    open fun bind(layoutResID: Int) {
        binding = DataBindingUtil.setContentView(this, layoutResID)
    }

    open fun changeFragment(
        fragment: androidx.fragment.app.Fragment,
        containerID: Int,
        addToBackStack: Boolean
    ) {
        val fragmentName = fragment.javaClass.name

        currentFragmentName = fragmentName
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerID, fragment, fragmentName)
        if (addToBackStack) transaction.addToBackStack(fragmentName)
        transaction.commit()
    }

    fun checkPermissions(
        permissions: Array<String>,
        permissionGranted: (() -> Unit)?,
        permissionDenied: (() -> Unit)?
    ) {
        this.permissionGranted = permissionGranted
        this.permissionDenied = permissionDenied

        val askForPermissions =
            permissions.any {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }

        if (askForPermissions) {
            ActivityCompat.requestPermissions(this, permissions,
                PERMISSIONS_REQUEST
            )
        } else {
            permissionGranted?.invoke()
        }
    }

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

    companion object {
        private const val PERMISSIONS_REQUEST = 111
        private val TAG = BaseActivity::class.java.simpleName
    }
}