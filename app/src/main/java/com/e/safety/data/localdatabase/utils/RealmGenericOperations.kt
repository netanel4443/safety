package com.e.safety.data.localdatabase.utils

import io.reactivex.rxjava3.core.Single
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import org.bson.types.ObjectId

/** Returns the desired [RealmObject], if is not existed , create it.
 * Should be wrapped with try & catch .
 * Use with [Realm.executeTransactionAsync] block
 */
//fun <T:RealmObject> getRlmObj(clazz: Class<T>, id:String, who: String,realm: Realm):T{
//
//            var rlmObj= realm.where(clazz).equalTo(id,who).findFirst()
//            if (rlmObj==null){
//                rlmObj= realm.createObject(clazz,who)
//            }
//    return rlmObj!!
//}

fun <T : RealmObject, V : RealmObject> Realm.deleteItemFromList(
    clazz: Class<V>,
    id: String,
    who: String,
    block: (V) -> T
): Single<String> {

    return Single.create { emitter ->
        try {
            executeTransactionAsync { realm ->
                val objWithList = realm.where(clazz).equalTo(id, who).findFirst()
                println(objWithList)
                val objectToDelete = block(objWithList!!)

                (objectToDelete).deleteFromRealm()
                emitter.onSuccess("Item deleted")

            }
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}

//todo check listener and results if it is handled correctly
fun <T : RealmObject, K:Any> Realm.getLists(
    clazz: Class<T>,
    block: (RealmResults<T>) -> K
): Single<K> {
    return Single.create { emitter ->
        var listener: RealmChangeListener<RealmResults<T>>? = null
        var res: RealmResults<T>? = null
        try {
            listener = object : RealmChangeListener<RealmResults<T>> {
                override fun onChange(results: RealmResults<T>) {
                    if (results.isLoaded) {
                        emitter.onSuccess(block(results))
                        res!!.removeChangeListener(this)
                        listener = null
                    }
                }
            }
            res = where(clazz).findAllAsync()
            res.addChangeListener(listener!!)
        } catch (e: Exception) {
            res!!.removeChangeListener(listener!!)
            listener = null
            emitter.onError(e)
        }
    }
}

fun <T:Any> Realm.rxSingleTransactionAsync(block: (Realm) -> T): Single<T> {
    return Single.create { emitter ->
        try {
            executeTransactionAsync { realm ->
                val message = block(realm)
                emitter.onSuccess(message)
            }

        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}


fun <T : RealmObject> Realm.findObjectById(
    clazz: Class<T>,
    id: ObjectId
): T? {
    return where(clazz).equalTo("_id", id).findFirst()
}

