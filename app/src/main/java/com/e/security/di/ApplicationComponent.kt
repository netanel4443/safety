package com.e.security.di

import android.app.Application
import com.e.security.di.scopes.ApplicationScope
import com.e.security.di.appsubcomponent.AppSubComponentModule
import com.e.security.di.components.MainActivityComponent
import com.e.security.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component
@ApplicationScope
@Component(modules = [
    AppSubComponentModule::class,
    AppModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    fun mainActivityComponent():MainActivityComponent.Factory

}