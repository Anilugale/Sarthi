package com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.*
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DailyVisitVM @Inject constructor(val service: Service) : ViewModel() {
    private var state: MutableStateFlow<DailyVisitStateList> = MutableStateFlow(DailyVisitStateList.Empty)
    val stateExpose = state.asStateFlow()

    init {
        getDailyVisitList()
    }
    fun getDailyVisitList(isFromRefresh:Boolean= false) {
        if (!isFromRefresh) {
            state.value = DailyVisitStateList.Process
        }
        viewModelScope.launch{
            if (WifiService.instance.isOnline()) {
                val response = service.getDailyVisitListFetch(ComplaintReq(Cache.loginUser!!.id))
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val data = response.body()!!.data
                            Cache.dailyVisitList.clear()
                            if (data.isEmpty()) {
                                state.value = DailyVisitStateList.Empty
                            } else {
                                Cache.dailyVisitList.addAll(response.body()!!.data)
                                state.value = DailyVisitStateList.Success(Cache.dailyVisitList)
                            }

                        } else {
                            state.value = DailyVisitStateList.Failed(response.message())
                        }
                    } else {
                        state.value = DailyVisitStateList.Failed("Server Error")
                    }
                }
            }else{
                state.value = DailyVisitStateList.Failed(Constants.NO_INTERNET)
            }
        }
    }

    fun deleteDailyWork(model: DeleteDailyVisitModel) {
        viewModelScope.launch{
            if (WifiService.instance.isOnline()) {
                val response = service.deleteDailyWork(model)
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            state.value =
                                DailyVisitStateList.SuccessDelete(response.body()!!.messages)
                        } else {
                            state.value =
                                DailyVisitStateList.FailedDelete(response.body()!!.messages)
                        }
                    } else {
                        state.value = DailyVisitStateList.FailedDelete("Server Error")
                    }
                }
            }else{
                state.value = DailyVisitStateList.FailedDelete(Constants.NO_INTERNET)
            }
        }
    }

}

sealed class DailyVisitStateList {
    class Success(val list :List<DailyVisitModel>) : DailyVisitStateList()
    object Process : DailyVisitStateList()
    object Empty : DailyVisitStateList()
    class Failed (val msg :String): DailyVisitStateList()
    class SuccessDelete(val msg :String) : DailyVisitStateList()
    class FailedDelete(val msg :String) : DailyVisitStateList()
}