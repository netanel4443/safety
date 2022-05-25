package com.e.security.ui.recyclerviews.celldata

import org.bson.types.ObjectId

data class FindingVhCell(
    var id:ObjectId= ObjectId(),
    var problem:String="",
    var findingSection:String="",
)