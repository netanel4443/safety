package com.e.security.ui.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

abstract class BaseActivity:AppCompatActivity() {

    @Inject lateinit var factory:ViewModelProvider.Factory

    protected inline fun <reified T: ViewModel> getViewModel() =
        ViewModelProvider(this,factory)[T::class.java]

    protected fun getTag(activity: BaseActivity):String{
        return activity.javaClass.name
    }

}