package com.e.security.ui.recyclerviews.celldata

import org.bson.types.ObjectId

data class StudyPlaceDataVhCell(
    var id:ObjectId=ObjectId(),
    var placeName:String="",
    var city:String=""
)