package com.e.security.ui.utils.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class MviLiveData<T> : LiveData<T> {

    constructor(t: T) : super(t)


    fun observe(owner: LifecycleOwner, observer: Observer<in T>, onObserve: (T) -> Unit) {
        super.observe(owner, observer)

        value?.let {
            onObserve.invoke(it)
        }
    }

    fun observe(owner: LifecycleOwner,onObserve:(T)->Unit,onDataChanged:(T,T)->Unit) {
        val observer = object :MviObserver<T>(){

            override fun onChanged(prev: T, current: T) {
                onDataChanged(prev,current)
            }

            override fun onFirstObserve(current: T) {
               onObserve(current)
            }
        }
        super.observe(owner, observer as Observer<in T>)
    }
}


