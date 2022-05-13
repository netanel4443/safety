package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener



open class GenericViewHolder<T>(view:View):RecyclerView.ViewHolder(view){
    protected open var itemClick: GenericItemClickListener<T>?=null
    protected var cachedItem:T?=null

    open fun setItemClickListener(itemClickListener: GenericItemClickListener<T>?){
        itemClick=itemClickListener
    }

    open fun bind(item:T){
        cachedItem=item
    }
}
