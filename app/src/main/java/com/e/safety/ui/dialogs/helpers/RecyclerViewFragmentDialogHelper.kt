package com.e.safety.ui.dialogs.helpers

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2

interface RecyclerViewFragmentDialogHelper<T : GenericVhItem> : GenericDialogHelper {
    fun observeState()
    fun getRecyclerViewAdapter(): GenericRecyclerviewAdapter2<T>
}