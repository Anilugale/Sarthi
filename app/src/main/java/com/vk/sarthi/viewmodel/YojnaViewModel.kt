package com.vk.sarthi.utli.com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.model.YojnaModel
import com.vk.sarthi.model.YojsnaPostReq
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class YojnaViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var state: MutableStateFlow<YojnaState> = MutableStateFlow(YojnaState.Empty)
    val stateExpose = state.asStateFlow()
    init {
        getYojnaList()
    }


    fun getYojnaList() {
        if (WifiService.instance.isOnline()) {
            state.value = YojnaState.Progress
            viewModelScope.launch(Dispatchers.IO) {
                val login = service.getYojnaList()
                viewModelScope.launch(Dispatchers.Main) {
                    if (login.isSuccessful) {
                        if (login.body() != null) {
                            state.value = YojnaState.Success(login.body()!!.data)
                        } else {
                            if (login.body() != null) {
                                state.value = YojnaState.Failed(login.body()!!.messages)
                            } else {
                                state.value = YojnaState.Failed(login.errorBody().toString())
                            }
                        }
                    }
                }
            }
        }else{
            state.value = YojnaState.Failed(Constants.NO_INTERNET)
        }
    }

    fun sendYojna(model: YojsnaPostReq) {
        if (WifiService.instance.isOnline()) {
            viewModelScope.launch(Dispatchers.IO) {
                val login = service.sendYojna(model)
                viewModelScope.launch(Dispatchers.Main) {
                    if (login.isSuccessful) {
                        if (login.body() != null) {
                            state.value = YojnaState.SuccessToast(login.body()!!.messages)
                        } else {
                            if (login.body() != null) {
                                state.value = YojnaState.Failed(login.body()!!.messages)
                            } else {
                                state.value = YojnaState.Failed(login.errorBody().toString())
                            }
                        }
                    }
                }
            }

        } else {
            state.value = YojnaState.Failed(Constants.NO_INTERNET)
        }
    }
}


sealed class YojnaState{
    class Failed(val msg:String):YojnaState()
    class SuccessToast(val msg:String):YojnaState()
    class Success(val list: ArrayList<YojnaModel>):YojnaState()
    object Empty:YojnaState()
    object Progress:YojnaState()
}
