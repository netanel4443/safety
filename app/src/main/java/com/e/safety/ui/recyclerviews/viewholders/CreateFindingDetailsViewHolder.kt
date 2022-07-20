package com.e.safety.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.safety.R
import com.e.safety.databinding.FindingRecyclerviewCellDesignBinding
import com.e.safety.ui.recyclerviews.celldata.FindingVhCell
import com.e.safety.ui.recyclerviews.generics.FindingVhItemClick
import com.e.safety.ui.recyclerviews.helpers.CreateVh
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

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