package com.e.safety.data.localdatabase.utils

import io.realm.RealmList

fun <E> ArrayList<E>.toRealmList(): RealmList<E> {
    val realmList = RealmList<E>()
    realmList.addAll(this)
    return realmList
}

fun <E> RealmList<E>.toArrayList(): ArrayList<E> {
    val arrayList = ArrayList<E>()
    forEach(arrayList::add)
    return arrayList
}