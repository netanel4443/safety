package com.e.security.ui.recyclerviews.clicklisteners

import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import org.apache.poi.ss.formula.functions.T
import org.bson.types.ObjectId

interface ReportVhItemClick:GenericItemClickListener<ReportVhCell> {

    fun onEditBtnClick(item: ReportVhCell)
    fun onLongClick(item: ReportVhCell):Boolean
}