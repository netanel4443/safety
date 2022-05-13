package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import com.e.security.databinding.ReportVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.clicklisteners.ReportVhItemClick
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class CreateStudyPlaceReportsVh : CreateVh<ReportVhCell> {
    override fun createViewHolder(
        view: View,
        itemClick: GenericItemClickListener<ReportVhCell>?
    ): GenericViewHolder<ReportVhCell> {
        val vh =ReportVh(view)
        vh.setItemClickListener(itemClick)
        return vh
    }

    private inner class ReportVh(view:View):GenericViewHolder<ReportVhCell>(view){
        private val binding: ReportVhCellDesignBinding = ReportVhCellDesignBinding.bind(view)
        private var itmClk:ReportVhItemClick<ReportVhCell>?=null

        override fun setItemClickListener(itemClickListener: GenericItemClickListener<ReportVhCell>?) {
            itmClk=itemClickListener as ReportVhItemClick<ReportVhCell>
        }

        init {
            binding.parent.setOnClickListener {
                itmClk!!.onItemClick(cachedItem!!)
            }
            binding.editBtn.setOnClickListener {
                itmClk!!.onEditBtnClick()
            }
        }

        override fun bind(item: ReportVhCell) {
           super.bind(item)
            binding.date.text=item.date
        }
    }

    }
