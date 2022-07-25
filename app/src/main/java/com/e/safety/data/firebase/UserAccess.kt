package com.e.safety.data.firebase

import com.e.safety.data.firebase.utils.rxGetData
import com.e.safety.data.internetconnection.InternetConnection
import com.e.safety.di.scopes.ApplicationScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@ApplicationScope
class UserAccess @Inject constructor() {

    private val fireStore = Firebase.firestore
    val collection = "enabled"

    fun checkIfAllowedToExportReport(): Maybe<Boolean> {
        return fireStore.collection(collection)
            .get()
            .rxGetData { result ->
                var data = false
                for (document in result) {
                    data = document.data["enabled"] as Boolean
                }
                data
            }.filter { it }
    }

}