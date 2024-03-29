package com.e.safety.data

import org.bson.types.ObjectId

data class FindingDataHolder(
    var id: ObjectId = ObjectId(),
    var priority: String = "0", //initial data
    var testArea: String = "",
    var sectionInAssessmentList: String = "",
    var problemLocation: String = "",
    var requirement: String = "",
    var problem: String = "",
    //todo decide if it is a list of uris or strings
    var problemImages: ArrayList<String> = ArrayList()
)

