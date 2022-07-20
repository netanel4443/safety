package com.e.safety.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.e.safety.ui.utils.livedata.MviLiveData
import com.e.safety.ui.utils.livedata.MviMutableLiveData2
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel<S> : ViewModel() {

    private val compositeDisposable=CompositeDisposable()

    protected  fun Disposable.addDisposable(){
        compositeDisposable.add(this)
    }

    protected abstract  val  _viewState: MviMutableLiveData2<S>
    val viewState:MviLiveData<S> get() = _viewState

    override fun onCleared() {
        super.onCleared()

     compositeDisposable.clear()
    }
}