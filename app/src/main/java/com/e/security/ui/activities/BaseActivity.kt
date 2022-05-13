package com.e.security.ui.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

abstract class BaseActivity:AppCompatActivity() {

    @Inject lateinit var factory:ViewModelProvider.Factory

    protected inline fun <reified T: ViewModel> getViewModel() =
        ViewModelProvider(this,factory)[T::class.java]

    protected  fun getTag(activity: BaseActivity):String{
        return activity.javaClass.name
    }

    protected var compositeDisposable= CompositeDisposable()

    protected  fun Disposable.addDisposable(){
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}