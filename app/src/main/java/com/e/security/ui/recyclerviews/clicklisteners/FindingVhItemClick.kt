package com.e.security.ui.recyclerviews.clicklisteners

import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

interface FindingVhItemClick:GenericItemClickListener<FindingVhCell> {

    fun onLongClick(item: FindingVhCell):Boolean
}