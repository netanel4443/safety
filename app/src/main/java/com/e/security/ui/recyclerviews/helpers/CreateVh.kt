package com.e.security.ui.recyclerviews.helpers

import android.view.View
import com.e.security.ui.recyclerviews.viewholders.GenericViewHolder

interface CreateVh<T> {
    fun  createViewHolder(view: View, itemClick: GenericItemClickListener<T>?): GenericViewHolder<T>
}