package com.e.safety.ui.utils.livedata

import androidx.lifecycle.Observer

interface OnObserveObserver<T>:Observer<T> {

    fun onObserve(t:T?)
}