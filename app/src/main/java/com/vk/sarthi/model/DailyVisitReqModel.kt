package com.vk.sarthi.model

data class DailyVisitReqModel(
    val birthdayinfo: String,
    val coordinatorid: String,
    val deathpersoninfo: String,
    val drinkingwaterinfo: String,
    val electricityinfo: String,
    val govservantinfo: String,
    val latitude: String,
    val longitude: String,
    val newschemes: String,
    val persons_visited: List<PersonsVisited>,
    val politicalinfo: String,
    val primarycarecenterinfo: String,
    val rashanshopinfo: String,
    val schoolinfo: String,
    val veterinarymedicineinfo: String,
    val villageid: String,
    val visitid: String,
    val watercanelinfo: String,
    val devinfo: String?,
    val otherinfo: String?,


)