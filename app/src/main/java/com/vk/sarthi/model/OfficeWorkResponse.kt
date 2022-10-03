package com.vk.sarthi.model

data class OfficeWorkResponse(
    val data: ArrayList<OfficeWorkModel>,
    val error: Any,
    val messages: String,
    val status: Int
)


data class CreateOfficeWorkResponse(
    val data: OfficeWorkModel,
    val error: Any,
    val messages: String,
    val status: Int
)


