package com.vk.sarthi.model

import com.vk.sarthi.utli.Constants

data class ComplaintResponse(
    val data: List<ComplaintModel>,
    val error: Any,
    val messages: String,
    val status: Int
)

data class ComplaintReq(val coordinatorid:Int,val page:Int,val size:Int = Constants.PageSize)
data class CoordinatoridMode(val coordinatorid:Int)