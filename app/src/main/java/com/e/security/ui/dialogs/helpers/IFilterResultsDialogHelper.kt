package com.e.security.ui.dialogs.helpers

import io.reactivex.rxjava3.core.Observable

interface IFilterResultsDialogHelper<T> :GenericDialogHelper {
    fun onItemClick(item:T)
    fun textChanges(charSequence: CharSequence):Observable<List<T>>
    fun onItemsReady(items:List<T>)

}