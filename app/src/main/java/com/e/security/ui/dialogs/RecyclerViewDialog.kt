package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.R
import com.e.security.databinding.RecyclerViewDialogBinding
import com.e.security.ui.dialogs.helpers.GenericDialogHelper
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2

open class RecyclerViewDialog<T : GenericVhItem>(private var context: Context) {

    private var alert: AlertDialog? = null
    private var recyclerviewAdapter: GenericRecyclerviewAdapter2<T>? = null
    private var binding: RecyclerViewDialogBinding? = null
    private var recyclerView: RecyclerView? = null

    open fun showDialog() {
        if (alert == null) {
            throw NullPointerException(context.getString(R.string.dialog_not_created))
        }
        show()
    }

    open fun create() {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        binding = RecyclerViewDialogBinding.inflate(inflater)

        setRecyclerView(binding!!.recyclerview)

        alertDialog.setView(binding!!.root)
        alert = alertDialog.create()
        alert!!.setCanceledOnTouchOutside(true)

    }

    protected fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun addItems(items: List<T>) {
        recyclerviewAdapter!!.submitList(items)
    }

    private fun show() {
        alert?.run {
            show()
        }
    }

    open fun dismissDialog() {
        alert?.run { dismiss() }
    }


    fun setRecyclerViewAdapter(adapter: GenericRecyclerviewAdapter2<T>): GenericRecyclerviewAdapter2<T> {
        recyclerviewAdapter = adapter
        recyclerView!!.adapter = adapter
        return adapter
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): RecyclerView {
        recyclerView!!.layoutManager = layoutManager
        return recyclerView!!
    }

    fun setFixedSize(): RecyclerView {
        recyclerView!!.setHasFixedSize(true)
        return recyclerView!!
    }

    fun setVerticalLinearLayoutManager() {
        setLinearLayoutManager(LinearLayoutManager.VERTICAL)
    }

    fun setHorizontalLinearLayoutManager() {
        setLinearLayoutManager(LinearLayoutManager.HORIZONTAL)
    }

    private fun setLinearLayoutManager(orientation: Int) {
        recyclerView!!.layoutManager = LinearLayoutManager(
            context,
            orientation,
            false
        )
    }
}

