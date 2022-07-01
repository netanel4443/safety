package com.e.security.data.objects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class FindingRlmObj:RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var testArea: String = ""
    var sectionInAssessmentList: String = ""
    var problemLocation: String = ""
    var requirement: String = ""
    var problem: String = ""
    var picPath: String = ""
    var priority:String="0"
}