package com.e.security.utils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable


fun <T> Observable<T>.subscribeBlock(block: (T) -> Unit): Disposable {
    return subscribe({
        block.invoke(it)
    }, ::printErrorIfDbg)
}


fun <T> Single<T>.subscribeBlock(block: (T) -> Unit): Disposable {
    return subscribe({
        block.invoke(it)
    }, ::printErrorIfDbg)
}

fun Completable.subscribeBlock(block: () -> Unit): Disposable {
    return subscribe({
        block.invoke()
    }, ::printErrorIfDbg)
}