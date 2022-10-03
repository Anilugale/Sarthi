package com.vk.sarthi.model

data class LoginResponse(
    val data: UserModel?,
    val error: Boolean,
    val messages: String,
    val status: Int
)

data class LoginReq(
    val username: String,
    val password: String,
    val deviceId: String,
)



data class ForgetPasswordOTPReq(
    val mobileno: String,
)

data class VerifyPassword(
    val mobileno: String,
    val password: String,
    val otp: String,
)
