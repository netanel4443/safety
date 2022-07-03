package com.e.security.di.components

import com.e.security.MainActivity
import com.e.security.di.modules.MainVmModule
import com.e.security.di.scopes.ActivityScope
import com.e.security.ui.dialogs.StudyPlaceInfoFscreen
import com.e.security.ui.fragments.CameraFragment
import com.e.security.ui.fragments.CreateFindingFragment
import com.e.security.ui.fragments.FindingsFragment
import com.e.security.ui.fragments.ReportsFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [
        MainVmModule::class]
)
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory{
        fun create(): MainActivityComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(studyPlaceInfoFscreen: StudyPlaceInfoFscreen)
    fun inject(createFindingFragment: CreateFindingFragment)
    fun inject(reportsFragment: ReportsFragment)
    fun inject(findingsFragment: FindingsFragment)
    fun inject(cameraFragment: CameraFragment)

}