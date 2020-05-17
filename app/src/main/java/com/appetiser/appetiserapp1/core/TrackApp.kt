package com.appetiser.appetiserapp1.core

import android.app.Application
import io.realm.Realm

class TrackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}