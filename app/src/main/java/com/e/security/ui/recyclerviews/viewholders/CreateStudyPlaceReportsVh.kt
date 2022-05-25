package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.security.R
import com.e.security.databinding.ReportVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.clicklisteners.ReportVhItemClick
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

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


