package com.vk.sarthi.model

data class YojsnaPostReq(
    val coordinatorid: Int,
    val village_id: String,
    val yojana: List<Int>
)


data class YojnaResponse(
    val data: YojnaModel,
    val error: Any,
    val messages: String,
    val status: Int
)