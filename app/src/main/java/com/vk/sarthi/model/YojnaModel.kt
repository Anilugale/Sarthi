package com.vk.sarthi.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

data class YojnaModel(
    val createddate: Any,
    val form: String,
    val id: Int,
    val schemename: String,
    val type: String
){
    var isCheck = mutableStateOf(false)

    init {
        isCheck = mutableStateOf(false)
    }
}


data class YojnaReponse(
    val data: ArrayList<YojnaModel>,
    val error: Any,
    val messages: String,
    val status: Int
)