package com.e.security.data.localdatabase.configuration

import io.realm.Realm
import io.realm.RealmConfiguration

class FindingsDetailsSchema {

    fun getRealmInstance(): Realm {
        val realmName = "FINDINGS_DETAILS_SCHEMA"
        val config: RealmConfiguration = RealmConfiguration.Builder().name(realmName).build()
        return Realm.getInstance(config)
    }

}