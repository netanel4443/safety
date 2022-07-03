package com.e.security.ui.utils.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.e.security.ui.utils.PrevAndCurrentState

open class MviLiveData<T> : LiveData<T> {

    constructor(t: T) : super(t)


    fun observe(owner: LifecycleOwner, observer: Observer<in T>, onObserve: (T) -> Unit) {
        super.observe(owner, observer)

        value?.let {
            onObserve.invoke(it)
        }
    }
}


