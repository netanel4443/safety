package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.databinding.FilterResultsDialogBinding
import com.e.security.ui.dialogs.helpers.IFilterResultsDialogHelper
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.utils.printErrorIfDbg
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class FilterResultsDialog<T>(
    private var context: Context,
    private var cVh: Class<out CreateVh<T>>
) {
    private var alert: AlertDialog? = null
    private var recyclerviewAdapter: GenericRecyclerviewAdapter<T, out CreateVh<T>>? = null
    private var helper: IFilterResultsDialogHelper<T>? = null
    private var filterObservable: Observable<List<T>>? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var binding: FilterResultsDialogBinding? = null

    fun showDialog() {
        if (alert == null) {
            create()
        }
        subscribeForFilter()
        show()
    }


    private fun create() {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        binding = FilterResultsDialogBinding.inflate(inflater)

        alertDialog.setView(binding!!.root)
        alert = alertDialog.create()
        alert?.setCanceledOnTouchOutside(true)
        alert?.setOnDismissListener { onDismiss() }

        binding!!.cancelButton.setOnClickListener { onDismiss() }

        recyclerviewAdapter = GenericRecyclerviewAdapter(cVh)
        recyclerviewAdapter?.setItemClickListener(object : GenericItemClickListener<T> {
            override fun onItemClick(item: T) {
                helper?.onItemClick(item)
                onDismiss()
            }
        })
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerview.adapter = recyclerviewAdapter
        binding!!.recyclerview.layoutManager = layoutManager
       // binding!!.recyclerview.setHasFixedSize(true)

        filterObservable = binding!!.editText.textChanges()
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { it.isNotBlank() }
            .switchMap { helper!!.textChanges(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        subscribeForFilter()
    }

    fun setIHelper(helper: IFilterResultsDialogHelper<T>) {
        this.helper = helper
    }

    fun addItems(items: List<T>) {
        recyclerviewAdapter!!.submitList(items)
    }

    private fun show() {
        helper?.onShowDialog()
        alert?.show()
    }

    private fun subscribeForFilter() {
        compositeDisposable.add(
            filterObservable!!.subscribe(
                helper!!::onItemsReady,
                ::printErrorIfDbg
            )
        )
    }

    private fun onDismiss() {
        compositeDisposable.clear()
        binding!!.editText.text.clear()
        helper?.onDismissDialog()
        alert!!.dismiss()
    }
}



