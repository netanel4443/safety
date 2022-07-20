package com.e.safety.ui.recyclerviews.clicklisteners

import com.e.safety.ui.recyclerviews.celldata.ReportVhCell
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

interface ReportVhItemClick:GenericItemClickListener<ReportVhCell> {

    fun onEditBtnClick(item: ReportVhCell)
    fun onLongClick(item: ReportVhCell):Boolean
}