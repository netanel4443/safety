package com.e.safety.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.safety.R
import com.e.safety.databinding.TextviewVhCellDesignBinding
import com.e.safety.ui.recyclerviews.celldata.TextViewVhCell
import com.e.safety.ui.recyclerviews.helpers.CreateVh
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

class CreateTextViewVh : CreateVh<TextViewVhCell>() {
    private var binding: TextviewVhCellDesignBinding? = null

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