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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfficeWorkViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()


    fun getList() {
        if (Cache.officeWorkModelList.isEmpty()) {
            viewModelScope.launch {
                if (WifiService.instance.isOnline()) {
                    state.value = Status.Process
                    val response = service.getOfficeWorkList(ComplaintReq(Cache.loginUser!!.id))

                    viewModelScope.launch(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            Cache.officeWorkModelList.addAll(response.body()!!.data)
                            if (Cache.officeWorkModelList.isEmpty()) {
                                state.value = Status.Empty
                            } else {
                                Cache.officeWorkModelList.reverse()
                                state.value = Status.SuccessOffice(Cache.officeWorkModelList, false)
                            }

                        } else {
                            if (response.body() != null) {
                                state.value = Status.Error(response.body()!!.messages)
                            } else {
                                state.value = Status.Error(Constants.Error)
                            }
                        }

                    }
                }else{
                    state.value = Status.Error(Constants.NO_INTERNET)
                }
            }
        } else {
            Cache.officeWorkModelList.sortBy { it.id }
            Cache.officeWorkModelList.reverse()
            state.value = Status.SuccessOffice(Cache.officeWorkModelList, false)
        }

    }

    fun deleteWork(list: ArrayList<OfficeWorkModel>, commentID: Int) {
        viewModelScope.launch {
            if (WifiService.instance.isOnline()) {
                state.value = Status.ProcessHeader(true)
                val deleteComment =
                    service.deleteOfficeWork(DeleteOfficeWorkModel(commentID, Cache.loginUser!!.id))
                state.value = Status.ProcessHeader(true)
                if (deleteComment.isSuccessful) {
                    for (it in list) {
                        if (it.id == commentID) {
                            list.remove(it)
                            break
                        }
                    }

                    state.value = Status.SuccessDelete(
                        deleteComment.body()!!.messages, list
                    )

                } else {
                    state.value = Status.FailedDelete(deleteComment.body()!!.messages)

                }

            }else {
                state.value = Status.FailedDelete(Constants.NO_INTERNET)
            }
        }

    }

}


sealed interface Status {
    object Process : Status
    data class Success(val list: ArrayList<ComplaintModel>, val isFooterShow: Boolean) : Status
    data class SuccessOffice(val list: ArrayList<OfficeWorkModel>, val isFooterShow: Boolean) :
        Status

    data class SuccessUser(val user: UserModel) : Status
    data class SuccessComment(val message:String) : Status
    object Empty : Status
    data class Error(val error: String) : Status
    data class ErrorLogin(val error: String) : Status
    data class SuccessDelete(val msg: String,val list: ArrayList<OfficeWorkModel>) : Status
    data class FailedDelete(val msg: String) : Status
    data class ProcessHeader(val isShow: Boolean) : Status
}
