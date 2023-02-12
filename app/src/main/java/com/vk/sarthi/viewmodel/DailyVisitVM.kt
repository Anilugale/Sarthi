package com.vk.sarthi.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.*
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DailyVisitVM @Inject constructor(val service: Service,@ApplicationContext val context :Context) : ViewModel() {
    private var state: MutableStateFlow<DailyVisitStateList> = MutableStateFlow(DailyVisitStateList.Empty)
    val stateExpose = state.asStateFlow()

    val isFooter = mutableStateOf(true)

    fun getDailyVisitList(isFromRefresh:Boolean= false,isFromPagination:Boolean) {
        if (!isFromRefresh) {
            isFooter.value = true
            if (!isFromPagination) {
                state.value = DailyVisitStateList.Process
            }
        }
        viewModelScope.launch{
            if(Cache.dailyVisitList.isEmpty() || isFromPagination) {
                if (WifiService.instance.isOnline()) {
                    val i = Cache.dailyVisitList.size / Constants.PageSize
                    if (isFromPagination) {
                        delay(500)
                    }

                    val response =
                        service.getDailyVisitListFetch(ComplaintReq(Cache.loginUser!!.id, i.inc()))
                    viewModelScope.launch(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                val data1 = response.body()!!.data
                                val data = data1
                                if (isFromRefresh) {
                                    Cache.dailyVisitList.clear()
                                }
                                if (data.isEmpty() && (Cache.dailyVisitList.isEmpty() && !isFromRefresh)) {
                                    state.value = DailyVisitStateList.Empty
                                } else {
                                    isFooter.value = data1.size >= Constants.PageSize
                                    Cache.dailyVisitList.addAll(data1)
                                    state.value = DailyVisitStateList.Success(Cache.dailyVisitList)
                                }

                            } else {
                                state.value = DailyVisitStateList.Failed(response.message())
                            }
                        } else {
                            state.value = DailyVisitStateList.Failed("Server Error")
                        }
                    }
                } else {
                    Cache.isOfflineDailyVisit(context)
                    state.value = DailyVisitStateList.OffLine(Constants.NO_INTERNET,Cache.dailyVisitOfflineList)
                }
            }else{
                state.value = DailyVisitStateList.Success(Cache.dailyVisitList)
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
    class OffLine(val msg: String, val list: ArrayList<VisitOffLineModel>): DailyVisitStateList()
    class SuccessDelete(val msg :String) : DailyVisitStateList()
    class FailedDelete(val msg :String) : DailyVisitStateList()
}