package com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.DailyVisitReqModel
import com.vk.sarthi.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddDailyVisitVM @Inject constructor(val service: Service) : ViewModel() {
    private var state: MutableStateFlow<DailyVisitState> = MutableStateFlow(DailyVisitState.Empty)
    val stateExpose = state.asStateFlow()

    fun setDailyVisitReq(model: DailyVisitReqModel) {
        state.value = DailyVisitState.Process
        viewModelScope.launch{
            val response = service.createDailyVisit(model)
            viewModelScope.launch(Dispatchers.Main) {
                if(response.isSuccessful ){
                    if (response.body()!=null) {
                        if(model.visitid.isNotEmpty()) {
                            Cache.dailyVisitList.removeIf { it.id.toString() == model.visitid }
                        }
                        Cache.dailyVisitList.add(response.body()!!.data)
                        state.value = DailyVisitState.Success(response.body()!!.messages)
                    }else{
                        state.value = DailyVisitState.Failed
                    }
                }else{
                    state.value = DailyVisitState.Failed
                }
            }
        }
    }
}

sealed class DailyVisitState {
    class Success(val msg :String) : DailyVisitState()
    object Process : DailyVisitState()
    object Empty : DailyVisitState()
    object Failed : DailyVisitState()
}