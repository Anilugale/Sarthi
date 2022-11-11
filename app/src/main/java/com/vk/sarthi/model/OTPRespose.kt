package com.vk.sarthi.model



data class OTPRespose(
    val `data`: List<Any>,
    val error: Any,
    val messages: String,
    val status: Int
)