package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.security.R
import com.e.security.databinding.HozerMankalVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

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
        binding = HozerMankalVhCellDesignBinding.bind(view!!)
        binding!!.parent.setOnClickListener {
            itemClick!!.onItemClick(cachedItem!!)
        }

    }
    override fun bindData(item: HozerMankalVhCell) {
        binding!!.definition.text = item.requirement
        binding!!.section.text = item.sectionInAssessmentList
    }
}