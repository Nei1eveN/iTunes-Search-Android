package com.appetiser.appetiserapp1.core.dao

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults

abstract class TrackBaseDao<T : RealmObject>(
    private val realm: Realm,
    private val clazz: Class<T>
) : DaoImpl<T> {

    override fun add(t: T): T? {
        var managedObject: T? = null
        realm.executeTransaction { transaction ->
            managedObject = transaction.copyToRealmOrUpdate(t)
        }
        return managedObject
    }

    override fun addAllAsync(list: List<T>) {
        realm.executeTransactionAsync { transaction ->
            transaction.copyToRealmOrUpdate(list)
        }
    }

    override fun remove(id: Int) {
        realm.where(clazz).equalTo("trackId", id).findFirst()
            ?.let { t -> realm.executeTransaction { t.deleteFromRealm() } }
    }

    override fun findById(id: Int): T? = realm.where(clazz).equalTo("trackId", id).findFirst()

    override fun findAll(): List<T> = realm.where(clazz).findAll()

    override fun findFirst(): T? = realm.where(clazz).findFirst()

    override fun findAllAsync(): RealmResults<T> = realm.where(clazz).findAllAsync()
}