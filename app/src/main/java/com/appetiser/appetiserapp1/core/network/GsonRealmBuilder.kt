package com.appetiser.appetiserapp1.core.network

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.realm.RealmObject

object GsonRealmBuilder {

    val builder: GsonBuilder
        get() = GsonBuilder()
                .registerTypeAdapter(Double::class.java,
                    DoubleTypeAdapter()
                )
                .setExclusionStrategies(object : ExclusionStrategy {
                    override fun shouldSkipField(f: FieldAttributes): Boolean {
                        return f.declaringClass == RealmObject::class.java
                    }

                    override fun shouldSkipClass(clazz: Class<*>): Boolean {
                        return false
                    }
                })

    fun get(): Gson {
        return builder.create()
    }
}