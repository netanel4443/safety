package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.security.R
import com.e.security.databinding.FindingRecyclerviewCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.clicklisteners.FindingVhItemClick
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class CreateFindingDetailsViewHolder : CreateVh<FindingVhCell>() {
    private var binding: FindingRecyclerviewCellDesignBinding? = null
    private var itmClick: FindingVhItemClick? = null
    override fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<FindingVhCell>?
    ):  GenericViewHolder {
        return createVh(
            parent,
            R.layout.finding_recyclerview_cell_design,
            itemClick
        )
    }

    override fun onInitVh(view: View) {
        binding = FindingRecyclerviewCellDesignBinding.bind(view)
        binding!!.parent.setOnClickListener {
            itmClick!!.onItemClick(cachedItem!!)
        }

        binding!!.parent.setOnLongClickListener {
            itmClick!!.onLongClick(cachedItem!!)
        }
    }

    override fun bindData(item: FindingVhCell) {
        binding!!.problem.text = item.problem
        binding!!.section.text = item.findingSection
    }

    override fun setClickListener(itemClickListener: GenericItemClickListener<FindingVhCell>?) {
        itmClick = itemClickListener as FindingVhItemClick
    }
}