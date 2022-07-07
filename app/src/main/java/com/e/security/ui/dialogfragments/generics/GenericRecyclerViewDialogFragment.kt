package com.e.security.ui.dialogfragments.generics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.databinding.RecyclerViewDialogBinding
import com.e.security.ui.dialogfragments.BaseRecyclerViewDialogFragment
import com.e.security.ui.dialogs.helpers.RecyclerViewFragmentDialogHelper
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2

open class GenericRecyclerViewDialogFragment<T : GenericVhItem> : BaseRecyclerViewDialogFragment() {

    private var binding: RecyclerViewDialogBinding? = null
    private var iHelper: RecyclerViewFragmentDialogHelper<T>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecyclerViewDialogBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        iHelper!!.observeState()
    }

    private fun initUi() {
        val recyclerView = binding!!.recyclerview
        val adapter = iHelper!!.getRecyclerViewAdapter()
        setRecyclerViewAdapter(recyclerView, adapter as GenericRecyclerviewAdapter2<GenericVhItem>)
        setVerticalLinearLayoutManager(recyclerView)
    }

    protected fun setIhelper(helper: RecyclerViewFragmentDialogHelper<T>) {
         iHelper = helper
    }

    //todo check about memory leaks and [iHelper] setting to null
    override fun onDetach() {
        super.onDetach()
        iHelper = null
    }
}