package com.e.safety.ui.utils.livedata

import androidx.lifecycle.Observer

abstract class MviObserver<T> : Observer<T> {
    private var prevValue: T? = null

    override fun onChanged(t: T) {
//        if no changes in data , return
//        if (prevValue == t) return

        onChanged(prevValue, t)
        // update prev value
        prevValue = t
    }

    abstract fun onChanged(prev: T?, current: T)

}