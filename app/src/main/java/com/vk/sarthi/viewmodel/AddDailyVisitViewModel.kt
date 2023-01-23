package com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.OfficeWorkModel
import com.vk.sarthi.service.Service
import com.vk.sarthi.ui.screen.WorkState
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
class AddDailyVisitViewModel @Inject constructor(private val service: Service): ViewModel() {
    private var state: MutableStateFlow<WorkState> = MutableStateFlow(WorkState.Empty)
    val stateExpose = state.asStateFlow()

    fun createWorkDetails(commentTxt: String, file: File?, model: OfficeWorkModel?) {
        if (WifiService.instance.isOnline()) {
            viewModelScope.launch(Dispatchers.IO) {
                val commentTxtP: RequestBody =
                    commentTxt.toRequestBody("text/plain".toMediaTypeOrNull())
                val coordinatorIdP: RequestBody =
                    Cache.loginUser!!.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val mobileNo: RequestBody =
                    Cache.loginUser!!.mobileno.toRequestBody("text/plain".toMediaTypeOrNull())

                val map: HashMap<String, RequestBody> = HashMap()
                map["coordinatorid"] = coordinatorIdP
                map["comment"] = commentTxtP
                map["usermobileno"] = mobileNo
                if (model != null) {
                    val workID: RequestBody =
                        model.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    map["work_id"] = workID
                }

                var body: MultipartBody.Part? = null
                if (file != null) {
                    val requestFile =
                        file.asRequestBody("image/jpg".toMediaType())
                    body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                }
                val response = service.createWorkTask(map, body)
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.data != null) {
                            if (model != null) {
                                Cache.officeWorkModelList.remove(model)
                            }
                            Cache.officeWorkModelList.add(response.body()!!.data)
                            state.value = WorkState.Success(response.body()!!.messages)
                        } else {
                            state.value = WorkState.Failed(response.body()!!.messages)
                        }
                    } else {
                        state.value = WorkState.Failed("Server Error")
                    }
                }
            }
        }else{
            state.value = WorkState.Failed(Constants.NO_INTERNET)
        }

    }
}
