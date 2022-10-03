package com.vk.sarthi.model

data class ComplaintResponse(
    val data: List<ComplaintModel>,
    val error: Any,
    val messages: String,
    val status: Int
)

data class ComplaintReq(val coordinatorid:Int)