package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import com.e.security.databinding.FindingRecyclerviewCellDesignBinding
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.ui.recyclerviews.celldata.FindingVhCellData

class CreateFindingDetailsViewHolder: CreateVh<FindingVhCellData> {
    override fun createViewHolder(
        view: View,
        itemClick: GenericItemClickListener<FindingVhCellData>?
    ): GenericViewHolder<FindingVhCellData> {

        val vh=FindingsDetailsViewHolder(view)
        vh.setItemClickListener(itemClick)
        return vh
    }

    private inner class FindingsDetailsViewHolder(view:View):GenericViewHolder<FindingVhCellData>(view){
        private val binding=FindingRecyclerviewCellDesignBinding.bind(view)

            init {
                binding.parent.setOnClickListener {
                    itemClick!!.onItemClick(cachedItem!!)
                }
            }
        override fun bind(item: FindingVhCellData) {
            super.bind(item)
            binding.problem.text=item.problem
            binding.section.text=item.findingSection
        }
    }
}