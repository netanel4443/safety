package com.e.security.di.modules

import android.app.Application
import android.content.res.Resources
import com.e.security.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @Provides
    @ApplicationScope
    fun provideResources(app: Application): Resources{
        return app.resources
    }
}