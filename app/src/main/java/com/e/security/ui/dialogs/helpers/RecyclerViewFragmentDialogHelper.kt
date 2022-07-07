package com.e.security.ui.dialogs.helpers

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2

interface RecyclerViewFragmentDialogHelper<T : GenericVhItem> : GenericDialogHelper {
    fun observeState()
    fun getRecyclerViewAdapter(): GenericRecyclerviewAdapter2<T>
}