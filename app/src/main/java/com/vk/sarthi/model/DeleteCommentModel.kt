package com.vk.sarthi.model

data class DeleteCommentModel(
    val comment_id: Int,
    val coordinatorid: Int
)


data class DeleteOfficeWorkModel(
    val work_id: Int,
    val coordinatorid: Int
)


data class DeleteDailyVisitModel(
    val visitid: Int,
    val coordinatorid: Int
)
