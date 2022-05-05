package com.e.security.data.definitions

data class HmScope(
    var area:String="",
    var section:String="",
    var definition:String="",
    var school:Boolean=true,
    var kindergarten:Boolean=true,
    var boardingSchool:Boolean=true,
    var youthVillage:Boolean=true,

)