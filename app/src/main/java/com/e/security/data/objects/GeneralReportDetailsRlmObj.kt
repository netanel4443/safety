package com.e.security.data.objects

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class GeneralReportDetailsRlmObj:RealmObject() {

    var city:String=""
    var placeName:String=""
    var institutionSymbol=""
    var date:String=""
    var testerDetails:String=""
    var ownership:String=""
    var studentsNumber:String=""
    var address:String=""
    var yearOfFounding:String=""
    var studyPlacePhone:String=""
    var managerDetails:String=""
    var inspectorDetails:String=""
    var studyPlaceParticipants:String=""
    var authorityParticipants:String=""


}