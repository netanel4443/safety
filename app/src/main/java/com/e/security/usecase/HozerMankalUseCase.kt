package com.e.security.usecase

import android.content.res.Resources
import com.e.security.R
import com.e.security.data.definitions.HmScope
import com.e.security.data.definitions.hozerMankal
import com.e.security.di.scopes.ActivityScope
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@ActivityScope
class HozerMankalUseCase @Inject constructor(
    private val resources: Resources
) {

    private var selectedHozerMankal = hozerMankal //  init it with general one
    private val schoolHozer = ArrayList<HmScope>()
    private val youthVillageHozer = ArrayList<HmScope>()
    private val boardingSchoolHozer = ArrayList<HmScope>()
    private val kindergartenHozer = ArrayList<HmScope>()

    fun sortHozerim(): Completable {
        return Completable.fromAction {
            hozerMankal.forEach {
                if (it.boardingSchool) {
                    boardingSchoolHozer.add(it)
                }
                if (it.school) {
                    schoolHozer.add(it)
                }
                if (it.kindergarten) {
                    kindergartenHozer.add(it)
                }
                if (it.youthVillage) {
                    youthVillageHozer.add(it)
                }
            }
        }
    }

    fun selectedHozerMankal(selectedHozer: String): Single<ArrayList<HmScope>> {
        return Single.fromCallable {
            if (selectedHozer == resources.getString(R.string.school)) {
                selectedHozerMankal = schoolHozer
            }
            if (selectedHozer == resources.getString(R.string.kindergarten)) {
                selectedHozerMankal = kindergartenHozer
            }
            if (selectedHozer == resources.getString(R.string.youth_village)) {
                selectedHozerMankal = youthVillageHozer
            }
            if (selectedHozer == resources.getString(R.string.boarding_school)) {
                selectedHozerMankal = boardingSchoolHozer
            }
            selectedHozerMankal
        }
    }

    fun filterHozerMankal(string:String): Observable<HmScope>{
        return Observable.fromIterable(selectedHozerMankal)
                 .filter{ it.definition.contains(string) }



    }


}