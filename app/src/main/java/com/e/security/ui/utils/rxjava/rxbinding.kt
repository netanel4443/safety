package com.e.security.ui.utils.rxjava

import android.view.View
import com.e.security.utils.printErrorIfDbg
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import java.util.concurrent.TimeUnit


fun  View.throttleClick(block: Consumer<Unit>):Disposable{
   return clicks().throttleFirst(2000,TimeUnit.MILLISECONDS)
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(block,::printErrorIfDbg)
}