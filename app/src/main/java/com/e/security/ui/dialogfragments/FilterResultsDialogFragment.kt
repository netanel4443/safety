package com.e.security.ui.dialogfragments

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.MainActivity
import com.e.security.ui.MainViewModel
import com.e.security.ui.dialogfragments.generics.GenericFilterResultsDialogFragment
import com.e.security.ui.dialogs.helpers.IFilterResultsDialogHelper
import com.e.security.ui.recyclerviews.adapters.HozerMankalRecyclerViewAdapter
import com.e.security.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import io.reactivex.rxjava3.core.Observable

class FilterResultsDialogFragment : GenericFilterResultsDialogFragment<GenericVhItem>() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
        setIhelper()
    }

    private fun setIhelper() {
        val iHelper = object : IFilterResultsDialogHelper<GenericVhItem> {

            override fun textChanges(charSequence: CharSequence): Observable<out List<GenericVhItem>> {
                return viewModel.filterHozerMankal(charSequence.toString())
            }

            override fun onItemsReady(items: List<GenericVhItem>) {
                viewModel.updateChosenHozerMankalRecyclerItems(items)
            }

            override fun getFilterResultsRecyclerViewAdapter(): GenericRecyclerviewAdapter2<GenericVhItem> {
                val adapter = HozerMankalRecyclerViewAdapter()

                adapter.setHozerMankalVhCellClickListener(object :
                    GenericItemClickListener<HozerMankalVhCell> {
                    override fun onItemClick(item: HozerMankalVhCell) {
                        dismiss()
                        viewModel.changeRequirement(item)
                    }
                })
                return adapter
            }

            override fun observeState() {
                viewModel.viewState.observe(viewLifecycleOwner) {
                    val items =
                        it.currentState.createFindingFragmentState.chosenHozerMankalRecyclerItems
                    submitList(items)
                }
            }
        }
        setIhelper(iHelper)
    }
}