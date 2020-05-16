package com.appetiser.appetiserapp1.core.dao

import io.realm.Realm
import io.realm.RealmObject

class RealmBaseDao<T : RealmObject>(
    private val realm: Realm,
    private val clazz: Class<T>
) {

}