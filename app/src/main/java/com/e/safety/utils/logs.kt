package com.e.safety.utils

import android.util.Log

const val DBG = true
fun printErrorIfDbg(tag: String, message: String?) {
    if (DBG) {
        Log.e(tag, message.toString())
    }
    println()

}

fun printErrorIfDbg(e: Throwable) {
    if (DBG) {
        Log.e(e.javaClass.name, e.message.toString())
    }
}

fun printIfDbg(tag: String, message: String?) {
    if (DBG) {
        Log.i(tag, message.toString())
    }
}