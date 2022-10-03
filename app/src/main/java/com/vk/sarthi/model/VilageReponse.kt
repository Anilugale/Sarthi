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
    val id: Int,
    val infourl: String,
    val taluka: String,
    val village: String
)