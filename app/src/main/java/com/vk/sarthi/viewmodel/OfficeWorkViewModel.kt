package com.vk.sarthi.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.*
import com.vk.sarthi.service.Service
import com.vk.sarthi.ui.screen.WorkState
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OfficeWorkViewModel @Inject constructor(private val service: Service,@ApplicationContext val context: Context) : ViewModel() {
    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()


    private var synchState: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val synchStateExpose = synchState.asStateFlow()
    val isFooter = mutableStateOf(true)
    fun getList(isFromPagination:Boolean) {
        if (Cache.officeWorkModelList.isEmpty() || isFromPagination) {
            viewModelScope.launch {
                if (WifiService.instance.isOnline()) {
                    val i = Cache.officeWorkModelList.size / Constants.PageSize
                    if (isFromPagination) {
                        delay(500)
                    }else{
                        state.value = Status.Process
                    }
                    val response = service.getOfficeWorkList(ComplaintReq(Cache.loginUser!!.id, i.inc()))
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!.data
                        isFooter.value = data.size >= Constants.PageSize
                        if (isFromPagination) {
                            state.value = Status.Empty
                        }
                        Cache.officeWorkModelList.addAll(data)
                        if (Cache.officeWorkModelList.isEmpty()) {
                            state.value = Status.Empty
                        } else {
                            state.value = Status.SuccessOffice(Cache.officeWorkModelList, false)
                        }
                    } else {
                        if (response.body() != null) {
                            state.value = Status.Error(response.body()!!.messages)
                        } else {
                            state.value = Status.Error(Constants.Error)
                        }
                    }
                } else {
                    Cache.isOfflineOfficeWork(context)
                    state.value = Status.OfflineData(Constants.NO_INTERNET,Cache.officeWorkOfflineList)
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

    fun syncData(officeWorkOfflineList: java.util.ArrayList<OfficeWorkOfflineModel>) {
       synchState.value = Status.ProcessDialog(true)
        val removeList = arrayListOf<OfficeWorkOfflineModel>()
        viewModelScope.launch {
            delay(1000)
           officeWorkOfflineList.forEach {
                if (!it.commentTxt.isNullOrEmpty()) {

                    val commentTxtP: RequestBody =
                        it.commentTxt.toRequestBody("text/plain".toMediaTypeOrNull())
                    val coordinatorIdP: RequestBody =
                        Cache.loginUser!!.id.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    val mobileNo: RequestBody =
                        Cache.loginUser!!.mobileno.toRequestBody("text/plain".toMediaTypeOrNull())

                    val map: HashMap<String, RequestBody> = HashMap()
                    map["coordinatorid"] = coordinatorIdP
                    map["comment"] = commentTxtP
                    map["usermobileno"] = mobileNo


                    var body: MultipartBody.Part? = null
                    if (!it.filePath.isNullOrEmpty()) {
                        val file = File(it.filePath)
                        val requestFile = file.asRequestBody("image/jpg".toMediaType())
                        body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    }
                    val response = service.createWorkTask(map,body)
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.data != null) {
                            Cache.officeWorkModelList.add(response.body()!!.data)
                            removeList.add(it)
                        }
                    }
                }else{
                    removeList.add(it)
                }
            }
            synchState.value = Status.ProcessDialog(false)
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
    data class OfflineData(val message:String,val list :ArrayList<OfficeWorkOfflineModel>) : Status
    object Empty : Status
    data class Error(val error: String) : Status
    data class ErrorLogin(val error: String) : Status
    data class SuccessDelete(val msg: String,val list: ArrayList<OfficeWorkModel>) : Status
    data class FailedDelete(val msg: String) : Status
    data class ProcessHeader(val isShow: Boolean) : Status
    data class ProcessDialog(val isShow: Boolean) : Status
}
