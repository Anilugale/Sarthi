package com.vk.sarthi.model

data class ComplaintModel(
    val attachments: String?,
    val categorie: String,
    val categorie_id: String,
    val comments: ArrayList<Comment>?,
    val coordinator_id: Int,
    val ticket_date: String,
    val ticket_exp: String,
    val ticket_id: Int,
    val ticket_status: String,
    val usermobileno: String,
    val isurgent:Int? = 0
)