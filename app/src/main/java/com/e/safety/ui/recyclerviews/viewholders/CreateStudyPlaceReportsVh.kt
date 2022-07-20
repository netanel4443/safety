package com.e.safety.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.safety.R
import com.e.safety.databinding.ReportVhCellDesignBinding
import com.e.safety.ui.recyclerviews.celldata.ReportVhCell
import com.e.safety.ui.recyclerviews.clicklisteners.ReportVhItemClick
import com.e.safety.ui.recyclerviews.helpers.CreateVh
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

class CreateStudyPlaceReportsVh : CreateVh<ReportVhCell>() {
    private var binding: ReportVhCellDesignBinding? = null
    private var itmClk: ReportVhItemClick? = null

    override fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<ReportVhCell>?
    ): GenericViewHolder {
        return createVh(parent, R.layout.report_vh_cell_design, itemClick)
    }

    override fun onInitVh(view: View) {
        binding = ReportVhCellDesignBinding.bind(view)
        binding!!.parent.setOnClickListener {
            itmClk!!.onItemClick(cachedItem!!)
        }
        binding!!.editBtn.setOnClickListener {
            itmClk!!.onEditBtnClick(cachedItem!!)
        }
        binding!!.parent.setOnLongClickListener {
            itmClk!!.onLongClick(cachedItem!!)
        }
    }

    override fun bindData(item: ReportVhCell) {
        binding!!.date.text = item.date
    }

    override fun setClickListener(itemClickListener: GenericItemClickListener<ReportVhCell>?) {
        itmClk = itemClickListener as ReportVhItemClick
    }
}


