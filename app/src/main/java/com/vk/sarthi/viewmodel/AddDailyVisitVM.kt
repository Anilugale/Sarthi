package com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.service.Service
import com.vk.sarthi.ui.screen.WorkState
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap
import javax.inject.Inject


@HiltViewModel
class AddDailyVisitVM @Inject constructor(val service: Service) : ViewModel() {
    private var state: MutableStateFlow<DailyVisitState> = MutableStateFlow(DailyVisitState.Empty)
    val stateExpose = state.asStateFlow()

    fun setDailyVisitReq(
        visitid: Int,
        model: HashMap<String, RequestBody>,
        birthdayFileBody: MultipartBody.Part?,
        rashanshopinfoBody: MultipartBody.Part?,
        electricInfoFileBody: MultipartBody.Part?,
        drinkingwaterinfofileBody: MultipartBody.Part?,
        watercanelinfofileBody: MultipartBody.Part?,
        schoolinfofileBody: MultipartBody.Part?,
        primaryHealthInfoFileBody: MultipartBody.Part?,
        vetarnityHealthInfoFileBody: MultipartBody.Part?,
        govInfoInfoFileBody: MultipartBody.Part?,
        politicalInfoFileBody: MultipartBody.Part?,
        deathPersonInfoFileBody: MultipartBody.Part?,
        newschemesfileBody: MultipartBody.Part?,
        devinfofileBody: MultipartBody.Part?,
        otherInfoFileBody: MultipartBody.Part?
    ) {
        if (WifiService.instance.isOnline()) {
            state.value = DailyVisitState.Process
            viewModelScope.launch {
                val response = service.createDailyVisit(
                    model, birthdayFileBody,
                    rashanshopinfoBody,
                    electricInfoFileBody,
                    drinkingwaterinfofileBody,
                    watercanelinfofileBody,
                    schoolinfofileBody,
                    primaryHealthInfoFileBody,
                    vetarnityHealthInfoFileBody,
                    govInfoInfoFileBody,
                    politicalInfoFileBody,
                    deathPersonInfoFileBody,
                    newschemesfileBody,
                    devinfofileBody,
                    otherInfoFileBody
                )
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (visitid != 0) {
                                Cache.dailyVisitList.removeIf { it.id == visitid }
                            }
                            Cache.dailyVisitList.add(response.body()!!.data)
                            state.value = DailyVisitState.Success(response.body()!!.messages)
                        } else {
                            state.value = DailyVisitState.Failed(response.message())
                        }
                    } else {
                        state.value = DailyVisitState.Failed(Constants.Error)
                    }
                }
            }
        }else{
            state.value = DailyVisitState.Failed(Constants.NO_INTERNET)
        }
    }
}

sealed class DailyVisitState {
    class Success(val msg: String) : DailyVisitState()
    object Process : DailyVisitState()
    object Empty : DailyVisitState()
    class Failed(val msg:String) : DailyVisitState()
}