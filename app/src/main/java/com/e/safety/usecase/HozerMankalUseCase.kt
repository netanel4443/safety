package com.e.safety.usecase

import android.content.res.Resources
import com.e.safety.R
import com.e.safety.data.definitions.HmScope
import com.e.safety.data.definitions.HozerMankalArray
import com.e.safety.di.scopes.ActivityScope
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@ActivityScope
class HozerMankalUseCase @Inject constructor(
    private val resources: Resources
) {

    private var selectedHozerMankal = ArrayList<HmScope>() //  init it with general one
    private val schoolHozer = ArrayList<HmScope>()
    private val youthVillageHozer = ArrayList<HmScope>()
    private val boardingSchoolHozer = ArrayList<HmScope>()
    private val kindergartenHozer = ArrayList<HmScope>()

    fun sortHozerim(): Completable {
        return Completable.fromAction {
            val hozerMankalArray = HozerMankalArray().create()
            selectedHozerMankal = hozerMankalArray // init with global one at first
            hozerMankalArray.forEach {
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

    fun selectedHozerMankal(selectedHozer: String): Single<HashMap<String, ArrayList<HmScope>>> {
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
        }.flatMap {
            Observable.fromIterable(it)
                .collectInto(HashMap(), ::collectHmScope)
        }
    }

    fun filterHozerMankal(string: String): Single<HashMap<String, ArrayList<HmScope>>> {

        return Observable.fromIterable(selectedHozerMankal)
            .filter {
                it.definition.contains(string) ||
                        it.section.contains(string) ||
                        it.testArea.contains(string)
            }.collectInto(HashMap(), ::collectHmScope)
    }

    private fun collectHmScope(
        hMap: HashMap<String, ArrayList<HmScope>>,
        hmScope: HmScope
    ): HashMap<String, ArrayList<HmScope>> {

        if (hMap.containsKey(hmScope.testArea)) {
            hMap[hmScope.testArea]!!.add(hmScope)
        } else {
            hMap[hmScope.testArea] = arrayListOf(hmScope)
        }
        return hMap
    }


}