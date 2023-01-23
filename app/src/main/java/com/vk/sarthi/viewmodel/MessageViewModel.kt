package com.vk.sarthi.utli.com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.ComplaintReq
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Constants
import com.vk.sarthi.utli.com.vk.sarthi.model.MessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val service: Service) : ViewModel() {

    private var state: MutableStateFlow<MsgListStatus> = MutableStateFlow(MsgListStatus.Progress)
    val stateExpose = state.asStateFlow()

    init {
        getVillageList()
    }

    private fun getVillageList() {
        if (WifiService.instance.isOnline()) {
            viewModelScope.launch {
                val msgList = service.getMsgList(ComplaintReq(Cache.loginUser!!.id))
                if (msgList.isSuccessful && msgList.body() != null) {
                    if (msgList.body()!!.status == 200) {
                        state.value = MsgListStatus.SuccessMsgList(msgList.body()!!.data)
                    } else {
                        state.value =
                            MsgListStatus.Error(msgList.body()!!.messages ?: Constants.Error)
                    }
                } else {
                    state.value = MsgListStatus.Error(Constants.Error)
                }

            }
        }else{
            state.value = MsgListStatus.Error(Constants.NO_INTERNET)
        }
    }


}

sealed class MsgListStatus{
    object Progress:MsgListStatus()
    object Empty:MsgListStatus()
    class SuccessMsgList(val msgList: List<MessageModel>):MsgListStatus()
    class Error(val msg: String):MsgListStatus()
}
