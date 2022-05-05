package com.e.security.utils

import android.util.Log

const val DBG=true
fun printErrorIfDbg(tag:String,message:String?){
    if (DBG){
        Log.e(tag,message.toString())
    }
    println()

}
fun printErrorIfDbg(e:Throwable){
    if (DBG){
        e.printStackTrace()
    }
}

fun printIfDbg(tag:String,message:String?){
    if (DBG){
        Log.i(tag,message.toString())
    }
}