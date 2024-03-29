package com.e.safety.ui.recyclerviews.adapters

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.R
import com.e.safety.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.safety.ui.recyclerviews.celldata.TextViewVhCell
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.generics.VhItemSetters
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.safety.ui.recyclerviews.viewholders.CreateHozerMankalVh
import com.e.safety.ui.recyclerviews.viewholders.CreateTextViewVh

class HozerMankalRecyclerViewAdapter : GenericRecyclerviewAdapter2<GenericVhItem>() {


    private val CREATE_TEXTVIEW_VH_CELL = 0
    private val CREATE_FINDING_VH_CELL = 1

    private val textVhItemSetter = VhItemSetters<TextViewVhCell>(
        layoutId =  R.layout.textview_vh_cell_design
    )

    private val hozerMankalVhItemSetter = VhItemSetters<HozerMankalVhCell>(
        layoutId =   R.layout.hozer_mankal_vh_cell_design,
    )


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