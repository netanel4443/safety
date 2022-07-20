package com.e.safety.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.safety.di.scopes.ActivityScope
import com.e.safety.di.scopes.ViewModelKey
import com.e.safety.di.viewmodelfactory.ViewModelProviderFactory
import com.e.safety.ui.viewmodels.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainVmModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainVm(viewModel: MainViewModel): ViewModel

    @Binds
    @ActivityScope
    abstract fun bindVmFactory(factory:ViewModelProviderFactory):ViewModelProvider.Factory
}