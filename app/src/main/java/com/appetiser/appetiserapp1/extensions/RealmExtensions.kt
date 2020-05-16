package com.appetiser.appetiserapp1.extensions

import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import org.intellij.lang.annotations.Flow

fun <T : RealmObject> T.toUnmanaged(): T {
    return if (this.isManaged) realm.copyFromRealm(this) else this
}

/**
 * Returns the model with the supplied [primaryKeyValue]
 *
 * @param T the class of the object which is to be queried for.
 * @param primaryKeyValue value of the primary key
 * @return a typed `RealmQuery`, which can be used to query for specific objects of this type.
 */
inline fun <reified T : RealmModel> Realm.find(primaryKeyValue: Int): T? {
    val primaryKey = schema[T::class.java.simpleName]?.primaryKey
        ?: throw IllegalArgumentException("${T::class.java.simpleName} does not have a primary key")

    return this.where(T::class.java).equalTo(primaryKey, primaryKeyValue).findFirst()
}

/**
 * Returns flow of un-managed realm data
 *
 * @return a type `Flow`, which can be used to observe realm notifications.
 */
/*
@ExperimentalCoroutinesApi
fun <T : RealmObject> RealmResults<T>.asCallbackFlow(): Flow<List<T>> {
    return callbackFlow {
        val listener = RealmChangeListener<RealmResults<T>> { results ->
            offer(results.map { it.toUnmanaged() }.toList())
        }
        this@asCallbackFlow.addChangeListener(listener)

        awaitClose {
            this@asCallbackFlow.removeChangeListener(listener)
            cancel("$this closed")
        }
    }
}*/
