package com.e.safety.ui.utils.livedata

class MviMutableLiveData2<T>(private val initialState: T) :
    MviLiveData<T>(initialState) {

    private var state: T = initialState

    private fun _mviValue(newState: T) {
        if (newState == state) return
        state = newState
        super.setValue(state)
    }

    private fun _postMviValue(newState: T) {
        if (newState == state) return
        state = newState
        super.postValue(state)
    }

    fun currentState(): T {
        return state
    }

    fun postValue(func: (T) -> T) {
        _postMviValue(func(state))
    }

    fun mviValue(func: (T) -> T) {
        _mviValue(func(state))
    }

}
