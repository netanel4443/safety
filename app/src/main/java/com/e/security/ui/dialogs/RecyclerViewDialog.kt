package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.databinding.RecyclerViewDialogBinding
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class RecyclerViewDialog<T>(
    private var context: Context,
    private var cVh: Class<out CreateVh<T>>,
    private val layoutId: Int
) {
    private var alert: AlertDialog? = null
    private var recyclerviewAdapter: GenericRecyclerviewAdapter<T, out CreateVh<T>>? = null
    var onClick:((T)->Unit)?=null

    fun showDialog() {
        alert?.let {
            show()
        } ?: create()


    }

    private fun create() {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        val binding = RecyclerViewDialogBinding.inflate(inflater)

        recyclerviewAdapter = GenericRecyclerviewAdapter(layoutId, cVh)
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


        alertDialog.setView(binding.root)
        alert = alertDialog.create()

        show()
    }

    fun addItems(items: List<T>) {
        recyclerviewAdapter!!.addItems(items)
    }

    private fun show() {
        alert?.run {
            show()
        }
    }
    }

