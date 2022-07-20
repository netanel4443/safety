package com.e.safety.di

import android.app.Application
import com.e.safety.di.scopes.ApplicationScope
import com.e.safety.di.appsubcomponent.AppSubComponentModule
import com.e.safety.di.components.MainActivityComponent
import com.e.safety.di.modules.AppModule
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