package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.databinding.FilterResultsDialogBinding
import com.e.security.ui.dialogs.helpers.IFilterResultsDialogHelper
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.utils.printErrorIfDbg
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class FilterResultsDialog<T : GenericVhItem>(private var context: Context)
    :RecyclerViewDialog<T>(context) {

    private var alert: AlertDialog? = null
    private var helper: IFilterResultsDialogHelper<T>? = null
    private var filterObservable: Observable<List<T>>? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var binding: FilterResultsDialogBinding? = null

    init {
        create()
    }

    override fun showDialog() {
        show()
    }

    override  fun create() {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        binding = FilterResultsDialogBinding.inflate(inflater)

        setRecyclerView(binding!!.recyclerview)

        alertDialog.setView(binding!!.root)
        alert = alertDialog.create()
        alert?.setCanceledOnTouchOutside(true)
        alert?.setOnDismissListener { dismissDialog() }

        binding!!.cancelButton.setOnClickListener { dismissDialog() }

        filterObservable = binding!!.editText.textChanges()
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { it.isNotBlank() }
            .switchMap { helper!!.textChanges(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun setIHelper(helper: IFilterResultsDialogHelper<T>) {
        this.helper = helper
    }


    private fun show() {
        helper?.onShowDialog()
        subscribeForFilter()
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

     override fun dismissDialog() {
        compositeDisposable.clear()
        binding!!.editText.text.clear()
        helper?.onDismissDialog()
        alert!!.dismiss()
    }

}



