package com.e.security.data.objects

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class StudyPlaceRlmObj:RealmObject() {
    @PrimaryKey
    var _id:ObjectId= ObjectId()
    var generalReportDetailsRlmObj: GeneralReportDetailsRlmObj?=null
    var reportList:RealmList<FindingListRlmObj> = RealmList()

}