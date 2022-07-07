package com.e.security.ui.dialogfragments.generics

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.databinding.FilterResultsDialogBinding
import com.e.security.ui.dialogfragments.BaseRecyclerViewDialogFragment
import com.e.security.ui.dialogs.helpers.IFilterResultsDialogHelper
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.utils.subscribeBlock
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

open class GenericFilterResultsDialogFragment<T : GenericVhItem> :
    BaseRecyclerViewDialogFragment() {

    private var filterObservable: Observable<List<T>>? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var binding: FilterResultsDialogBinding? = null
    private var iHelper: IFilterResultsDialogHelper<T>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterResultsDialogBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initUi()
        iHelper!!.observeState()

        filterObservable = binding!!.editText.textChanges()
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { it.isNotBlank() }
            .switchMap { iHelper!!.textChanges(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        subscribeForFilter()
    }

    private fun initUi() {
        binding!!.cancelButton.setOnClickListener {
            dismiss()
        }
        val recyclerView = binding!!.recyclerview
        val adapter = iHelper!!.getFilterResultsRecyclerViewAdapter()
        setRecyclerViewAdapter(recyclerView, adapter as GenericRecyclerviewAdapter2<GenericVhItem>)
        setVerticalLinearLayoutManager(recyclerView)
    }

    private fun subscribeForFilter() {
        compositeDisposable.add(
            filterObservable!!.subscribeBlock(iHelper!!::onItemsReady)
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        compositeDisposable.clear()
        binding!!.editText.text.clear()
    }

    protected fun setIhelper(helper: IFilterResultsDialogHelper<T>) {
        iHelper = helper
    }

    override fun onDetach() {
        super.onDetach()
        iHelper = null
    }
}