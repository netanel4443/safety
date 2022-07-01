package com.e.security.ui.recyclerviews.generics

import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

interface FindingVhItemClick : GenericItemClickListener<FindingVhCell> {
    override fun onItemClick(item: FindingVhCell) {}
    fun onLongClick(item: FindingVhCell): Boolean

}