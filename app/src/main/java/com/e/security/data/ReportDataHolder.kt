package com.e.security.data

import org.bson.types.ObjectId

class ReportDataHolder {

    var id: ObjectId = ObjectId()
    var date: String = ""

    //the size is according to priority : index 0 - priority 0 , index 1 - priority 1 etc.
    var findingArr = Array<HashMap<ObjectId, FindingDataHolder>>(3) { HashMap() }
    var conclusion: String = ""

}