package com.e.safety.ui.dialogs.helpers

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import io.reactivex.rxjava3.core.Observable

interface IFilterResultsDialogHelper<T : GenericVhItem> : GenericDialogHelper {
    fun textChanges(charSequence: CharSequence): Observable<out List<T>>
    fun onItemsReady(items: List<T>)
    fun getFilterResultsRecyclerViewAdapter(): GenericRecyclerviewAdapter2<T>
    fun observeState()
}