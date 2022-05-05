package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import com.e.security.databinding.ReportVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
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
        val binding: ReportVhCellDesignBinding = ReportVhCellDesignBinding.bind(view)

        init {
            binding.parent.setOnClickListener {
                itemClick!!.onItemClick(cachedItem!!)
            }
        }

        override fun bind(item: ReportVhCell) {
           super.bind(item)
            binding.date.text=item.date
        }
    }

    }
