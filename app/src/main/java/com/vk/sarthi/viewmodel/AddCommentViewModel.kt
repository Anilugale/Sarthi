package com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.ComplaintModel
import com.vk.sarthi.service.Service
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
class AddCommentViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()

    fun createComment(
        ticketId: String,
        coordinatorId: String,
        commentTxt: String,
        imageUrl: File?,
        comment: ComplaintModel?,
        commentId: String,

        ) = viewModelScope.launch {

        state.value = Status.Process
        viewModelScope.launch(Dispatchers.IO) {
            val ticketIdP: RequestBody =  ticketId.toRequestBody("text/plain".toMediaTypeOrNull())
            val coordinatorIdP: RequestBody =  coordinatorId.toRequestBody("text/plain".toMediaTypeOrNull())
            val commentTxtP: RequestBody = commentTxt.toRequestBody("text/plain".toMediaTypeOrNull())
            val mobileNo: RequestBody = Cache.loginUser!!.mobileno.toRequestBody("text/plain".toMediaTypeOrNull())

            val map: HashMap<String, RequestBody> = HashMap()
            map["ticket_id"] = ticketIdP
            map["coordinatorid"] = coordinatorIdP
            map["comment"] = commentTxtP
            map["usermobileno"] = mobileNo
            if (commentId != "0") {
                val commentID: RequestBody = commentId.toRequestBody("text/plain".toMediaTypeOrNull())
                map["comment_id"] = commentID
            }
            var body: MultipartBody.Part? = null
            if (imageUrl!=null) {
                val requestFile =
                    imageUrl.asRequestBody("image/jpg".toMediaType())
                body  = MultipartBody.Part.createFormData("file", imageUrl.name, requestFile)
            }
            val response = service.createComment(map,body)

            viewModelScope.launch(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body1 = response.body()
                    if(body1!=null) {
                        comment?.apply {
                            if (commentId != "0") {
                                for (it in comments!!) {
                                    if(it.comment_id == commentId.toInt()){
                                        comments.remove(it)
                                        comments.add(body1.data)
                                        break
                                    }
                                }
                            }else{
                                comments!!.add(body1.data)
                            }
                        }
                        state.value = Status.SuccessComment(body1.messages)

                    }else{
                        state.value = Status.Error(response.body()!!.messages)
                    }
                }else{
                    state.value = Status.Error("Error")
                }
            }
        }
    }
}

