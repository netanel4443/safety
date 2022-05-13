package com.e.security.data.localdatabase.migrations

import com.e.security.data.objects.StudyPlaceRlmObj
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import io.realm.RealmSchema

class Migrations : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion=oldVersion

        if (oldVersion==0L){
            migration1to2(realm.schema)
            oldVersion++
        }

    }

    private fun migration1to2(schema: RealmSchema) {
        val studyPlaceSchema = schema.get(StudyPlaceRlmObj::class.java.simpleName)
        studyPlaceSchema?.run {
            addField("educationalInstitution", String::class.java,FieldAttribute.REQUIRED).transform{
                it.set("educationalInstitution","");
            }
        }

    }
}