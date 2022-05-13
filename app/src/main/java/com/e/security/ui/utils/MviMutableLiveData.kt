package com.e.security.ui.utils

import com.e.security.ui.utils.livedata.MviLiveData

class MviMutableLiveData<T>(private var initialState: T) :
        MviLiveData<PrevAndCurrentState<T>>(PrevAndCurrentState(initialState,initialState)) {

    private var state:T=initialState


    private fun _mviValue(newState:T){
        val prevAndCurrentState= PrevAndCurrentState(state,newState)
        state=newState
        super.setValue(prevAndCurrentState)
    }

    private fun _postMviValue(newState: T){
        val prevAndCurrentState= PrevAndCurrentState(state,newState)
        state=newState
        super.postValue(prevAndCurrentState)
    }

    fun currentState():T{
        return state
    }

    fun postValue(func: (T) -> T){
        _postMviValue( func(state) )
    }

    fun mviValue(func:(T)->T){
        _mviValue( func(state) )
    }

}
