package com.e.safety.ui.recyclerviews.celldata

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import org.bson.types.ObjectId

data class StudyPlaceDataVhCell(
    override var id:Any=ObjectId(),
    var placeName:String="",
    var city:String=""
):GenericVhItem