package com.e.safety.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.databinding.RecyclerViewDialogBinding
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2

open class RecyclerViewDialog<T : GenericVhItem>(private var context: Context) :
    BaseAlertDialog(context) {

    private var recyclerviewAdapter: GenericRecyclerviewAdapter2<T>? = null
    private var binding: RecyclerViewDialogBinding? = null
    private var recyclerView: RecyclerView? = null

    init {
        create()
    }

    private fun create() {
        val inflater = LayoutInflater.from(context)
        binding = RecyclerViewDialogBinding.inflate(inflater)
        super.create(binding!!)
        setRecyclerView(binding!!.recyclerview)
    }

    protected fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun addItems(items: List<T>) {
        recyclerviewAdapter!!.submitList(items)
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

