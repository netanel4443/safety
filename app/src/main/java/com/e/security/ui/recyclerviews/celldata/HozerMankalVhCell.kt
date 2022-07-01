package com.e.security.ui.recyclerviews.celldata

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem

data class HozerMankalVhCell(
    override var id: Any,
    var requirement: String = "",
    var sectionInAssessmentList: String = "",
    var testArea: String = ""
):GenericVhItem


