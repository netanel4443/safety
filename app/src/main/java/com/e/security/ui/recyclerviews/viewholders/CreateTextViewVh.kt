package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.security.R
import com.e.security.databinding.TextviewVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class CreateTextViewVh : CreateVh<TextViewVhCell>() {
    private var binding: TextviewVhCellDesignBinding? = null

    override fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<TextViewVhCell>?
    ): GenericViewHolder {
        return createVh(
            parent,
            R.layout.textview_vh_cell_design,
            itemClick
        )
    }

    override fun onInitVh(view: View) {
        binding = TextviewVhCellDesignBinding.bind(view)
        binding!!.parent.setOnClickListener {
            itemClick?.onItemClick(cachedItem!!)
        }
    }


    override fun bindData(item: TextViewVhCell) {
        binding!!.tview.text = item.item
    }
}