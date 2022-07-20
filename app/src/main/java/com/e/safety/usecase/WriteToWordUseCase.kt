package com.e.safety.usecase

import android.net.Uri
import com.e.safety.data.ReportDataHolder
import com.e.safety.data.StudyPlaceDetailsDataHolder
import com.e.safety.data.writetopdf.WriteToPdf
import com.e.safety.data.writetoword.WriteToWord
import com.e.safety.di.scopes.ActivityScope
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@ActivityScope
class WriteToWordUseCase @Inject constructor(
    private val word: WriteToWord,
    private val pdf: WriteToPdf
) {

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