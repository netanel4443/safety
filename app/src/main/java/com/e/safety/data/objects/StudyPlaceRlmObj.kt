package com.e.safety.data.objects

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class StudyPlaceRlmObj:RealmObject() {
    @PrimaryKey
    var _id:ObjectId= ObjectId()
    var generalReportDetailsRlmObj: GeneralReportDetailsRlmObj?=null
    var reportList:RealmList<ReportRlmObj> = RealmList()
    var educationalInstitution:String=""

}