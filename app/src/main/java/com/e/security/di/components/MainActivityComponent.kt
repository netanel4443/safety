package com.e.security.di.components

import com.e.security.MainActivity
import com.e.security.di.modules.MainVmModule
import com.e.security.di.scopes.ActivityScope
import com.e.security.ui.dialogs.StudyPlaceInfoDialog
import com.e.security.ui.fragments.CameraFragment
import com.e.security.ui.fragments.CreateFindingFragment
import com.e.security.ui.fragments.FindingsDetailsFragment
import com.e.security.ui.fragments.StudyPlaceReportsFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [MainVmModule::class]
)
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory{
        fun create(): MainActivityComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(studyPlaceInfoDialog: StudyPlaceInfoDialog)
    fun inject(createFindingFragment: CreateFindingFragment)
    fun inject(studyPlaceReportsFragment: StudyPlaceReportsFragment)
    fun inject(findingsDetailsFragment: FindingsDetailsFragment)
    fun inject(cameraFragment: CameraFragment)
}