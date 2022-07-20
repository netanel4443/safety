package com.e.safety.ui.dialogfragments

import android.content.Context
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.dialogfragments.generics.GenericFilterResultsDialogFragment
import com.e.safety.ui.dialogs.helpers.IFilterResultsDialogHelper
import com.e.safety.ui.recyclerviews.adapters.HozerMankalRecyclerViewAdapter
import com.e.safety.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener
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
                        viewModel.changeRequirement(item)
                        dismiss()
                    }
                })
                return adapter
            }

            override fun observeState() {
                viewModel.viewState.observe(viewLifecycleOwner) {
                    val items =
//                        it.currentState.createFindingFragmentState.chosenHozerMankalRecyclerItems
                        it.createFindingFragmentState.chosenHozerMankalRecyclerItems
                    submitList(items)
                }
            }
        }
        setIhelper(iHelper)
    }
}