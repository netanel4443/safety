package com.e.safety.ui.recyclerviews.celldata

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import org.bson.types.ObjectId

data class ReportVhCell(
    override var id:Any,
    var date:String,
    var isLoading:Boolean = false
):GenericVhItem