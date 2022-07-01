package com.e.security.ui.recyclerviews.adapters

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell
import com.e.security.ui.recyclerviews.generics.FindingVhItemClick
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.ui.recyclerviews.generics.VhItemSetters
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.ui.recyclerviews.viewholders.CreateFindingDetailsViewHolder
import com.e.security.ui.recyclerviews.viewholders.CreateHozerMankalVh
import com.e.security.ui.recyclerviews.viewholders.CreateTextViewVh

class HozerMankalRecyclerViewAdapter : GenericRecyclerviewAdapter2<GenericVhItem>() {


    private val CREATE_TEXTVIEW_VH_CELL = 0
    private val CREATE_FINDING_VH_CELL = 1

    private val textVhItemSetter = VhItemSetters<TextViewVhCell>()
    private val hozerMankalVhItemSetter = VhItemSetters<HozerMankalVhCell>()


    init {
        textVhItemSetter.createVh = CreateTextViewVh::class.java
        hozerMankalVhItemSetter.createVh = CreateHozerMankalVh::class.java
    }

    override fun getItemViewType(position: Int): Int {
        val item = listDiffer.currentList[position]
        return when (item) {
            is TextViewVhCell -> CREATE_TEXTVIEW_VH_CELL
            else -> CREATE_FINDING_VH_CELL
        }
    }

    override fun getVhItemSetter(viewType: Int) {
        val vhItemSetter = when (viewType) {
            CREATE_TEXTVIEW_VH_CELL -> textVhItemSetter
            else -> hozerMankalVhItemSetter
        }
        setVhItemSetter(vhItemSetter as VhItemSetters<GenericVhItem>)
    }

    fun setHozerMankalVhCellClickListener(listener: GenericItemClickListener<HozerMankalVhCell>) {
        hozerMankalVhItemSetter.clickListener = listener
    }


}