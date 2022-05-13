package com.e.security.ui.utils.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class MviLiveData<T>:LiveData<T> {

    constructor(t:T):super(t)



    fun observe(owner: LifecycleOwner, observer: Observer<in T>,block:(T)->Unit) {
        super.observe(owner, observer)
        value?.let{
            block.invoke(it)
        }
    }


}
