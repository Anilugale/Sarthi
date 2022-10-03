package com.vk.sarthi.model

data class DailyVisitResponse(
    val data: DailyVisitModel,
    val error: Any,
    val messages: String,
    val status: Int
)


data class DailyVisitListResponse(
    val data: List<DailyVisitModel>,
    val error: Any,
    val messages: String,
    val status: Int
)


data class DailyVisitModel(
    val birthdayinfo: String,
    val coordinator_id: Int,
    val createddate: String,
    val deathpersoninfo: String,
    val drinkingwaterinfo: String,
    val electricityinfo: String,
    val govservantinfo: String,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val newschemes: String,
    val persons_visited: List<PersonsVisited>,
    val politicalinfo: String,
    val primarycarecenterinfo: String,
    val rashanshopinfo: String,
    val schoolinfo: String,
    val updated_at: String?,
    val veterinarymedicineinfo: String,
    val villageid: Int,
    val watercanelinfo: String,
    val devinfo: String,
    val otherinfo: String
)