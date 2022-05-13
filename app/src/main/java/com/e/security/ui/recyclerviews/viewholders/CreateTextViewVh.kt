package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import com.e.security.databinding.ReportVhCellDesignBinding
import com.e.security.databinding.TextviewVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class CreateTextViewVh : CreateVh<TextViewVhCell> {
    override fun createViewHolder(
        view: View,
        itemClick: GenericItemClickListener<TextViewVhCell>?
    ): GenericViewHolder<TextViewVhCell> {
        val binding=TextviewVhCellDesignBinding.bind(view)
        val vh=TextViewVh(binding)
        vh.setItemClickListener(itemClick)
        return vh
    }

    private inner class TextViewVh(private val binding:TextviewVhCellDesignBinding)
        :GenericViewHolder<TextViewVhCell>(binding.root){

        init {
            binding.parent.setOnClickListener {
                itemClick!!.onItemClick(cachedItem!!)
            }
        }

        override fun bind(item: TextViewVhCell) {
            super.bind(item)
            binding.tview.text=item.item
        }
    }
}