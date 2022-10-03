package com.vk.sarthi.model

data class AddCommentResponse(
    val data: Comment,
    val error: Any,
    val messages: String,
    val status: Int
)

