package com.appetiser.appetiserapp1.core.dao

import io.realm.RealmObject
import io.realm.RealmResults

interface DaoImpl<T : RealmObject> {

    fun add(t: T): T?

    fun addAllAsync(list: List<T>)

    fun remove(id: Int)

    fun findById(id: Int): T?

    fun findAll(): List<T>

    fun findFirst(): T?

    fun findAllAsync(): RealmResults<T>
}