package com.e.safety.di.appsubcomponent

import com.e.safety.di.components.MainActivityComponent
import dagger.Module

@Module(
    subcomponents = [MainActivityComponent::class]
)
class AppSubComponentModule {
}