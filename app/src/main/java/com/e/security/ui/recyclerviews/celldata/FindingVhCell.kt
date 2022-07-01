package com.e.security.ui.recyclerviews.celldata

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import org.bson.types.ObjectId

data class FindingVhCell(
   override var id:Any= ObjectId(),
    var problem:String="",
    var findingSection:String="",
):GenericVhItem