package com.e.security.di.appsubcomponent

import com.e.security.di.components.MainActivityComponent
import dagger.Module

@Module(
    subcomponents = [MainActivityComponent::class]
)
class AppSubComponentModule {
}