package com.e.safety.ui.recyclerviews.generics

import com.e.safety.ui.recyclerviews.celldata.FindingVhCell
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

interface FindingVhItemClick : GenericItemClickListener<FindingVhCell> {
    override fun onItemClick(item: FindingVhCell) {}
    fun onLongClick(item: FindingVhCell): Boolean

}