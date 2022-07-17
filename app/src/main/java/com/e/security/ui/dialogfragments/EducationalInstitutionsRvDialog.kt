package com.e.security.ui.dialogfragments

import android.content.Context
import com.e.security.ui.activities.mainactivity.MainActivity
import com.e.security.ui.viewmodels.MainViewModel
import com.e.security.ui.dialogfragments.generics.GenericRecyclerViewDialogFragment
import com.e.security.ui.dialogs.helpers.RecyclerViewFragmentDialogHelper
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.ui.recyclerviews.generics.VhItemSetters
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.ui.recyclerviews.viewholders.CreateTextViewVh

class EducationalInstitutionsRvDialog : GenericRecyclerViewDialogFragment<TextViewVhCell>() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
        setIhelper()
    }

    private fun setIhelper() {
        val iHelper = object : RecyclerViewFragmentDialogHelper<TextViewVhCell> {

            override fun getRecyclerViewAdapter(): GenericRecyclerviewAdapter2<TextViewVhCell> {
                val adapter = GenericRecyclerviewAdapter2<TextViewVhCell>()

                val setter = VhItemSetters<TextViewVhCell>()
                setter.createVh = CreateTextViewVh::class.java
                setter.clickListener = object :
                    GenericItemClickListener<TextViewVhCell> {
                    override fun onItemClick(item: TextViewVhCell) {
                        dismiss()
                        viewModel.changeEducationalInstitution(item)
                    }
                }
                adapter.setVhItemSetter(setter)

                return adapter
            }

            override fun observeState() {
                viewModel.viewState.observe(viewLifecycleOwner) {
                    val items = it.currentState.educationalInstitutionsRvItems
                    submitList(items)
                }
            }
        }
        setIhelper(iHelper)
    }
}