package com.e.security.usecase

import android.app.Application
import android.net.Uri
import com.e.security.data.ReportDataHolder
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.data.writetopdf.WriteToPdf
import com.e.security.data.writetoword.WriteToWord
import com.e.security.di.scopes.ActivityScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class WriteToWordUseCase @Inject constructor(
    private val application: Application
) {
    private val word = WriteToWord(application)
    private val pdf = WriteToPdf(application)

    fun getWordFileType(): Single<String> {
        return word.exportData()
    }


    fun saveWordFile(
        fileUri: Uri, reportDataHolder: ReportDataHolder,
        studyPlaceDetailsDataHolder: StudyPlaceDetailsDataHolder
    ): Single<String> {
        return word.save(fileUri, reportDataHolder, studyPlaceDetailsDataHolder)
    }

    fun exportPdf(
        reportDataHolder: ReportDataHolder,
        studyPlaceDetailsDataHolder: StudyPlaceDetailsDataHolder
    ): Single<String> {
        return pdf.createPdf(reportDataHolder, studyPlaceDetailsDataHolder)
    }

    fun savePdfFile(
        fileUri: Uri
    ): Single<String> {
        return pdf.save(fileUri)
    }
}