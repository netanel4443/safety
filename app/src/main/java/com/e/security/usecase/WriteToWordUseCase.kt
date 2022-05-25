package com.e.security.usecase

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.e.security.data.ReportDataHolder
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.data.writetoword.WriteToWord
import com.e.security.di.scopes.ActivityScope
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@ActivityScope
class WriteToWordUseCase @Inject constructor(
    private val application: Application
) {
        private val word=WriteToWord(application)
    fun write(): Single<Intent> {
        return word.exportData()
    }

    fun saveFile(fileUri: Uri, reportDataHolder: ReportDataHolder,
                 studyPlaceDetailsDataHolder: StudyPlaceDetailsDataHolder):Single<String>{
     return   word.save(fileUri,reportDataHolder,studyPlaceDetailsDataHolder)
    }
}