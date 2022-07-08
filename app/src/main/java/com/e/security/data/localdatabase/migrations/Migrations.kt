package com.e.security.data.localdatabase.migrations

import com.e.security.data.objects.FindingRlmObj
import com.e.security.data.objects.ReportRlmObj
import io.realm.*

class Migrations : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var old = oldVersion

        if (old == 0L) {
            migration0to1(realm.schema)
            old++
        }

        if (old == 2L) {
            migration1to2(realm.schema)
            old++
        }

        if (old == 3L) {
            migration2to3(realm.schema)
            old++
        }
    }

    private fun migration0to1(schema: RealmSchema) {
        val studyPlaceSchema = schema.get(FindingRlmObj::class.java.simpleName)
        studyPlaceSchema?.run {
            addField("problemLocation", String::class.java, FieldAttribute.REQUIRED).transform {
                it.set("problemLocation", "");
            }
        }
    }

    private fun migration1to2(schema: RealmSchema) {
        val reportSchema = schema.get(ReportRlmObj::class.java.simpleName)
        reportSchema?.run {
            addField("conclusion", String::class.java, FieldAttribute.REQUIRED).transform {
                it.set("conclusion", "");
            }
        }
    }

    private fun migration2to3(schema: RealmSchema) {
        val findingSchema = schema.get(FindingRlmObj::class.java.simpleName)
        findingSchema?.run {

            addRealmListField("picPaths", String::class.java).transform {
                val picPath = it.getString("picPath")
                it.getList("picPaths", String::class.java).add(picPath)
            }
            removeField("picPath")
        }
    }

}