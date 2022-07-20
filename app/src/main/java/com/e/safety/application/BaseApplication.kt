package com.e.safety.application

import android.app.Application
import com.e.safety.di.ApplicationComponent
import com.e.safety.di.DaggerApplicationComponent
import io.realm.Realm

class BaseApplication : Application() {

    val appComponent: ApplicationComponent =
        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()


    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}