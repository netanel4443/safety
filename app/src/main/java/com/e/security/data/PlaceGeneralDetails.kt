package com.e.security.data

import org.bson.types.ObjectId

data class PlaceGeneralDetails(
    var id:ObjectId=ObjectId(),
    var city:String="",
    var placeName:String="",
    var institutionSymbol:String="",
    var date:String="",
    var testerDetails:String=""
)
