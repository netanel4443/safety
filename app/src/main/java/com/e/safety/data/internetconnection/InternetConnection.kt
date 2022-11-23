package com.e.safety.data.internetconnection

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.e.safety.di.scopes.ApplicationScope
import com.e.safety.utils.subscribeBlock
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@ApplicationScope
class InternetConnection @Inject constructor(private val application: Application) {

    enum class InternetState {
        ACTIVE, INACTIVE
    }

    private val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    private val _behaviorSubject = BehaviorSubject.createDefault(InternetState.INACTIVE)
    val behaviorSubject get() = _behaviorSubject

    init {
        checkForInternet()
    }

    private fun checkForInternet() {
        Observable.interval(5, TimeUnit.SECONDS)
            .map { cm!!.activeNetwork != null  }
            .map { isActive ->
                if (isActive) {
                    InternetState.ACTIVE
                } else {
                    InternetState.INACTIVE
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock { networkState->
                _behaviorSubject.onNext(networkState)
            }
    }

}