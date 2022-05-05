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
}