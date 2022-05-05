package com.e.security.data

import org.bson.types.ObjectId

data class FindingDataHolder(
    var id: ObjectId = ObjectId(),
    var priority:String ="0",//initial data
    var section:String="",
    var sectionInAssessmentList:String="",
    var requirement:String="",
    var problem:String="",
    var pic:String=""
)

