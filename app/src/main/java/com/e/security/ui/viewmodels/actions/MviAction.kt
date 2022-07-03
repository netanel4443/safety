package com.e.security.ui.viewmodels.actions

import io.reactivex.rxjava3.core.Observable

abstract class MviAction {

    private val observableArray:ArrayList<Observable<IMviAction>> = ArrayList()

    fun addAction(action: Observable<IMviAction>){
        observableArray.add(action)
    }

    fun invokeActions(){

    }
}