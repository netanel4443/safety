package com.e.safety.ui.utils.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.rxjava3.core.Observable

fun <T : Any> LiveData<T>.toObservable(lifecycleOwner: LifecycleOwner): Observable<T> = Observable
    .fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))

