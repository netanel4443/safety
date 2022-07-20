package com.e.safety.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.safety.R
import com.e.safety.databinding.HozerMankalVhCellDesignBinding
import com.e.safety.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.safety.ui.recyclerviews.helpers.CreateVh
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

class CreateHozerMankalVh : CreateVh<HozerMankalVhCell>() {
    private var binding: HozerMankalVhCellDesignBinding? = null

    override fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<HozerMankalVhCell>?
    ): GenericViewHolder {
        return createVh(
            parent,
            R.layout.hozer_mankal_vh_cell_design,
            itemClick
        )
    }

    override fun onInitVh(view: View) {
        binding = HozerMankalVhCellDesignBinding.bind(view)
        binding!!.parent.setOnClickListener {
            itemClick!!.onItemClick(cachedItem!!)
        }

    }
    override fun bindData(item: HozerMankalVhCell) {
        binding!!.definition.text = item.requirement
        binding!!.section.text = item.sectionInAssessmentList
    }
}