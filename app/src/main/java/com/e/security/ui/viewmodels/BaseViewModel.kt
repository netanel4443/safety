package com.e.security.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.e.security.ui.utils.MviMutableLiveData
import com.e.security.ui.utils.PrevAndCurrentState
import com.e.security.ui.utils.livedata.MviLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel<S> : ViewModel() {

    private val compositeDisposable=CompositeDisposable()

    protected  fun Disposable.addDisposable(){
        compositeDisposable.add(this)
    }

    protected abstract  val  _viewState:MviMutableLiveData<S>
    val viewState:MviLiveData<PrevAndCurrentState<S>> get() = _viewState

    override fun onCleared() {
        super.onCleared()

     compositeDisposable.clear()
    }
}