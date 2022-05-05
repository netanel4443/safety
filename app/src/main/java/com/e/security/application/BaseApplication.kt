package com.e.security.application

import android.app.Application
import com.e.security.di.ApplicationComponent
import com.e.security.di.DaggerApplicationComponent
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