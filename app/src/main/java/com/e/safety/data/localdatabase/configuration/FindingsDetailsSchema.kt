package com.e.safety.data.localdatabase.configuration

import com.e.safety.data.localdatabase.migrations.Migrations
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration

class FindingsDetailsSchema {

    private val lastVersion: Long = 4L

    fun getRealmInstance(): Realm {
        val realmName = "FINDINGS_DETAILS_SCHEMA"
        val migration: RealmMigration = Migrations()
        val config: RealmConfiguration = RealmConfiguration.Builder()
            .schemaVersion(lastVersion)
            .migration(migration)
            .name(realmName)
            .build()

        return Realm.getInstance(config)
    }

}