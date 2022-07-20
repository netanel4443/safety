package com.e.safety.di.components

import com.e.safety.di.modules.MainVmModule
import com.e.safety.di.scopes.ActivityScope
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.compose.CreateFindingScreen
import com.e.safety.ui.dialogfragments.EducationalInstitutionsRvDialog
import com.e.safety.ui.dialogfragments.FilterResultsDialogFragment
import com.e.safety.ui.dialogfragments.ImageOptionsDialog
import com.e.safety.ui.dialogfragments.ReportFragmentMenuRvDialog
import com.e.safety.ui.dialogfragments.basedialogfragments.BaseEditTextDialogFragment
import com.e.safety.ui.dialogfragments.basedialogfragments.BaseTextDialogFragment
import com.e.safety.ui.fragments.*
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [
        MainVmModule::class]
)
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(studyPlaceInfoFragment: StudyPlaceInfoFragment)
    fun inject(createFindingFragment: CreateFindingFragment)
    fun inject(reportsFragment: ReportsFragment)
    fun inject(findingsFragment: FindingsFragment)
    fun inject(cameraFragment: CameraFragment)
    fun inject(filterResultsDialogFragment: FilterResultsDialogFragment)
    fun inject(educationalInstitutionsRvDialog: EducationalInstitutionsRvDialog)
    fun inject(imageOptionsDialog: ImageOptionsDialog)
    fun inject(reportFragmentMenuRvDialog: ReportFragmentMenuRvDialog)
    fun inject(createFindingScreen: CreateFindingScreen)
    fun inject(breateFindingScreen: BaseTextDialogFragment)
    fun inject(baseEditTextDialogFragment: BaseEditTextDialogFragment)

}