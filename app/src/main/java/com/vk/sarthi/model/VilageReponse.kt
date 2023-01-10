package com.vk.sarthi.model

data class VilageReponse(
    val data: VillageData,
    val error: Any,
    val messages: String,
    val status: Int
)

data class VillageData(
    val taluka: List<String>,
    val villages: List<Village>
)



data class Village(
    val commercial_person: List<CommercialPerson>,
    val coordinatorid: Int,
    val gan: String,
    val gat: String,
    val govment_servant: List<OfficialPerson>,
    val id: Int,
    val infourl: String,
    val karyakarte: Any,
    val official_person: List<OfficialPerson>,
    val sanstha_servant: List<OfficialPerson>,
    val taluka: String,
    val village: String
)

data class CommercialPerson(
    val address: String,
    val information: String,
    val mobile: String,
    val name: String,
    val type: String
)



data class OfficialPerson(
    val address: String,
    val information: String,
    val mobile: String,
    val name: String
)