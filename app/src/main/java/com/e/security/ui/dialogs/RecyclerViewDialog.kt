package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.databinding.RecyclerViewDialogBinding
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class RecyclerViewDialog<T>(
    private var context: Context,
    private var cVh: Class<out CreateVh<T>>
) {
    private var alert: AlertDialog? = null
    private var recyclerviewAdapter: GenericRecyclerviewAdapter<T, out CreateVh<T>>? = null
    var onClick: ((T) -> Unit)? = null

    fun showDialog(func: (T) -> Unit) {
        if (alert == null) {
            create()
        }
        onClick = func
        show()

    }

    private fun create() {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        val binding = RecyclerViewDialogBinding.inflate(inflater)

        alertDialog.setView(binding.root)
        alert = alertDialog.create()
        alert!!.setCanceledOnTouchOutside(true)

        recyclerviewAdapter = GenericRecyclerviewAdapter(cVh)
        recyclerviewAdapter!!.setItemClickListener(object : GenericItemClickListener<T> {
            override fun onItemClick(item: T) {
                onClick?.invoke(item)
                alert!!.dismiss()
            }
        })
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.adapter = recyclerviewAdapter
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.setHasFixedSize(true)
    }

    fun addItems(items: List<T>) {
        if (!recyclerviewAdapter!!.hasNoItems()) {
            recyclerviewAdapter!!.removeAllItems()
        }
        recyclerviewAdapter!!.addItems(items)
    }

    private fun show() {
        alert?.run {
            show()
        }
    }
}

