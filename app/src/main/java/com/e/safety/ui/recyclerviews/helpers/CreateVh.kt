package com.e.safety.ui.recyclerviews.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class CreateVh<T> {
    protected var view: View? = null
    protected var cachedItem: T? = null
    protected var itemClick: GenericItemClickListener<T>? = null

    abstract fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<T>?
    ): GenericViewHolder

    protected fun createVh(
        parent: ViewGroup,
        layoutId: Int,
        itemClick: GenericItemClickListener<T>?
    ): GenericViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        view = inflater.inflate(layoutId, parent, false)
        val vh = GenericViewHolder(view!!)
        vh.setItemClickListener(itemClick)
        return vh
    }

    protected abstract fun bindData(item: T)

    protected abstract fun onInitVh(view: View)

    protected open fun setClickListener(
        itemClickListener: GenericItemClickListener<T>?
    ) {
        itemClick=itemClickListener
    }


    inner class GenericViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        init {
            onInitVh(view)
        }

        fun setItemClickListener(itemClickListener: GenericItemClickListener<T>?) {
            setClickListener(itemClickListener)
        }

        fun bind(item: T) {
            cachedItem = item
            bindData(item)
        }
    }
}