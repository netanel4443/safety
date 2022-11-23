package com.e.safety.ui.utils.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class MviLiveData<T> : LiveData<T> {

    constructor(t: T) : super(t)


    fun observeWithInitialValue(
        owner: LifecycleOwner,
        onObserve: ((T) -> Unit)? = null,
        onDataChanged: (T?, T) -> Unit
    ) {
        val observer = object : MviObserver<T>() {

            override fun onChanged(prev: T?, current: T) {
                onDataChanged(prev, current)
            }

        }
        super.observe(owner, observer as Observer<in T>)

        value?.let {
            onObserve?.invoke(it)
        }
    }

    fun observeMviLiveData(owner: LifecycleOwner, onDataChanged: (T?, T) -> Unit) {
        val observer = object : MviObserver<T>() {

            override fun onChanged(prev: T?, current: T) {
                onDataChanged(prev, current)
            }
        }
        super.observe(owner, observer as Observer<in T>)
    }
}


