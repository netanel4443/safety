package com.e.safety.data.firebase.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.rxjava3.core.Single


fun <TResult,R> Task<TResult>.rxGetData(successBlock:(TResult)->R):Single<R>{
   return Single.create { emitter->
       addOnSuccessListener {
           val r = successBlock(it)
           emitter.onSuccess(r)
       }
       addOnFailureListener {
           emitter.onError(it)
       }
   }
}






