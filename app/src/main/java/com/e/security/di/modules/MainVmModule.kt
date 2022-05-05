package com.e.security.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.security.di.scopes.ActivityScope
import com.e.security.di.scopes.ViewModelKey
import com.e.security.di.viewmodelfactory.ViewModelProviderFactory
import com.e.security.ui.MainViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainVmModule {


    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainVm(viewModel:MainViewModel): ViewModel

    @Binds
    @ActivityScope
    abstract fun bindVmFactory(factory:ViewModelProviderFactory):ViewModelProvider.Factory
}