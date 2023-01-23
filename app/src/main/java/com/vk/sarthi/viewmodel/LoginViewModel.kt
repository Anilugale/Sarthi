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
class LoginViewModel @Inject constructor(private val service: Service) : ViewModel() {


    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()
    fun login(loginReq: LoginReq) {
        viewModelScope.launch (Dispatchers.IO) {
            if (WifiService.instance.isOnline()) {
                state.value = Status.Process
                viewModelScope.launch(Dispatchers.Main) {
                    val login = service.loginUrl(loginReq)
                    if (login.isSuccessful) {
                        if (login.body() != null && login.body()!!.data != null) {
                            state.value = Status.SuccessUser(login.body()!!.data!!)
                        } else {
                            if (login.body() != null) {
                                state.value = Status.ErrorLogin(login.body()!!.messages)
                            } else {
                                state.value = Status.ErrorLogin(login.errorBody().toString())
                            }
                        }
                    }
                }
            }else{
                state.value = Status.ErrorLogin(Constants.NO_INTERNET)
            }
        }
    }


}


@HiltViewModel
class MainViewModel @Inject constructor(private val service: Service) : ViewModel() {


    private var state: MutableStateFlow<VillageState> = MutableStateFlow(VillageState.Empty)
    val stateExpose = state.asStateFlow()

    init {
        getVillageList()
    }
    fun getVillageList() {
        if (Cache.villageData==null) {
            viewModelScope.launch  {
                if (WifiService.instance.isOnline()) {
                    viewModelScope.launch(Dispatchers.Main) {
                        try {
                            val response =
                                service.getVillageList(VillageReq(coordinator_id = Cache.loginUser!!.id))
                            if (response.isSuccessful) {
                                if (response.body() != null && response.body()!!.data != null) {
                                    Cache.villageData = response.body()!!.data
                                    state.value = VillageState.Success
                                } else {
                                    state.value = VillageState.Failed(response.message())
                                }
                            } else {
                                state.value = VillageState.Failed(response.message())
                            }
                        } catch (e: Exception) {
                            state.value = VillageState.Failed("Server Error")
                        }

                    }
                }else{
                    state.value = VillageState.Failed(Constants.NO_INTERNET)
                }
            }
        }else{
            state.value = VillageState.Empty
            state.value = VillageState.Success
        }
    }
}

sealed class VillageState{
    class Failed(val msg:String):VillageState()
    object Success:VillageState()
    object Empty:VillageState()
}
