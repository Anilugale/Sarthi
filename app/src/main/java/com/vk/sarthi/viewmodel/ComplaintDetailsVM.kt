package com.vk.sarthi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.Comment
import com.vk.sarthi.model.ComplaintModel
import com.vk.sarthi.model.DeleteCommentModel
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ComplaintDetailsVM @Inject constructor(val service: Service) : ViewModel() {
    private var state: MutableStateFlow<DetailsState> = MutableStateFlow(DetailsState.Empty)
    val stateExpose = state.asStateFlow()

    fun getList(isProgress: Boolean = false, comment: ComplaintModel?) {
        val comments = comment?.comments
        if (isProgress) {
            viewModelScope.launch {
                state.value = DetailsState.Success(comments!!)
            }
        } else {
            state.value = DetailsState.Success(comments!!)
        }
    }

    fun deleteComment(comment: ComplaintModel?, commentID: Int) {
        if (WifiService.instance.isOnline()) {
            viewModelScope.launch {
                val deleteComment =
                    service.deleteComment(DeleteCommentModel(commentID, Cache.loginUser!!.id))
                delay(1000)

                if (deleteComment.isSuccessful) {
                    for (it in comment?.comments!!) {
                        if (it.comment_id == commentID) {
                            comment.comments.remove(it)
                            break
                        }
                    }
                    state.value = DetailsState.SuccessDelete(
                        deleteComment.body()!!.messages, comment.comments
                    )

                } else {
                    state.value = DetailsState.FailedDelete(deleteComment.body()!!.messages)

                }
            }
        }else{
            state.value = DetailsState.FailedDelete(Constants.NO_INTERNET)
        }

    }
}

sealed class DetailsState {
    class Success(val commentList: ArrayList<Comment>) : DetailsState()
    class SuccessDelete(val msg: String, val commentList: ArrayList<Comment>) : DetailsState()
    class FailedDelete(val msg: String) : DetailsState()
    object Process : DetailsState()
    object Empty : DetailsState()
}