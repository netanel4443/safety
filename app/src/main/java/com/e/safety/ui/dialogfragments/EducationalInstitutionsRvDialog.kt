package com.e.safety.ui.dialogfragments

import android.content.Context
import android.view.WindowManager
import com.e.safety.R
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.dialogfragments.generics.GenericRecyclerViewDialogFragment
import com.e.safety.ui.dialogs.helpers.RecyclerViewFragmentDialogHelper
import com.e.safety.ui.recyclerviews.celldata.TextViewVhCell
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.generics.VhItemSetters
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.safety.ui.recyclerviews.viewholders.CreateTextViewVh

class EducationalInstitutionsRvDialog : GenericRecyclerViewDialogFragment<TextViewVhCell>() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
        setIhelper()
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setIhelper() {
        val iHelper = object : RecyclerViewFragmentDialogHelper<TextViewVhCell> {

            override fun getRecyclerViewAdapter(): GenericRecyclerviewAdapter2<TextViewVhCell> {
                val adapter = GenericRecyclerviewAdapter2<TextViewVhCell>()

                val setter = VhItemSetters<TextViewVhCell>(
                    layoutId =  R.layout.textview_vh_cell_design
                )
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
//                    val items = it.currentState.educationalInstitutionsRvItems
                    val items = it.educationalInstitutionsRvItems
                    submitList(items)
                }
            }
        }
        setIhelper(iHelper)
    }

}