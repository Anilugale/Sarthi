package com.vk.sarthi.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.*
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Constants
import com.vk.sarthi.utli.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import java.util.ArrayList
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(private val service: Service) : ViewModel() {


    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()
    fun login(loginReq: LoginReq) {
        viewModelScope.launch(Dispatchers.IO) {
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
            } else {
                state.value = Status.ErrorLogin(Constants.NO_INTERNET)
            }
        }
    }


}


@HiltViewModel
class MainViewModel @Inject constructor(private val service: Service,private val pref: SharedPreferences,@ApplicationContext val context :Context) : ViewModel() {


    private var state: MutableStateFlow<VillageState> = MutableStateFlow(VillageState.Empty)
    val stateExpose = state.asStateFlow()

    private var syncState: MutableStateFlow<SyncState> = MutableStateFlow(SyncState.Success)
    val syncStateExpose = syncState.asStateFlow()

    init {
        getVillageList()
    }

    fun getVillageList() {
        if (Cache.villageData == null) {
            viewModelScope.launch {
                if (WifiService.instance.isOnline()) {
                    viewModelScope.launch(Dispatchers.Main) {
                        try {
                            val response =
                                service.getVillageList(VillageReq(coordinator_id = Cache.loginUser!!.id))
                            if (response.isSuccessful) {
                                if (response.body() != null) {
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
                } else {
                    state.value = VillageState.Failed(Constants.NO_INTERNET)
                }
            }
        } else {
            state.value = VillageState.Empty
            state.value = VillageState.Success
        }
    }

    fun sendOffLineData() {

        viewModelScope.launch {
            var isOfficeDataSync = false
            syncState.value = SyncState.Processing
            if (Cache.officeWorkOfflineList.isNotEmpty()) {
                isOfficeDataSync = syncData(Cache.officeWorkOfflineList)
            }else{
                isOfficeDataSync = Cache.officeWorkOfflineList.isEmpty()
            }
            var isDailyVisitDataSync = false
            if (Cache.dailyVisitOfflineList.isNotEmpty()) {
                isDailyVisitDataSync = syncDataDaily(Cache.dailyVisitOfflineList)
            }else{
                isDailyVisitDataSync = Cache.dailyVisitOfflineList.isEmpty()
            }

            if (isOfficeDataSync && isDailyVisitDataSync) {
                Util.deleteRecursive(context.cacheDir)
                syncState.value = SyncState.Success
             }else{
                syncState.value = SyncState.Failed("Not Success")
            }

        }
    }

   private suspend fun  syncDataDaily(list: ArrayList<VisitOffLineModel>):Boolean {
        list.forEach { model->
            val map = HashMap<String, RequestBody>()
            model.hashMap.forEach {
                map[it.key] = it.value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
            var birthdayFileBody: MultipartBody.Part? = null
            var rashanshopinfoBody: MultipartBody.Part? = null
            var electricInfoFileBody: MultipartBody.Part? = null
            var drinkingwaterinfofileBody: MultipartBody.Part? = null
            var watercanelinfofileBody: MultipartBody.Part? = null
            var schoolinfofileBody: MultipartBody.Part? = null
            var primaryHealthInfoFileBody: MultipartBody.Part? = null
            var vetarnityHealthInfoFileBody: MultipartBody.Part? = null
            var govInfoInfoFileBody: MultipartBody.Part? = null
            var politicalInfoFileBody: MultipartBody.Part? = null
            var deathPersonInfoFileBody: MultipartBody.Part? = null
            var newschemesfileBody: MultipartBody.Part? = null
            var devinfofileBody: MultipartBody.Part? = null
            var otherInfoFileBody: MultipartBody.Part? = null



            if (model.birthdayFileBody.isNotEmpty()) {
                val file = File(model.birthdayFileBody)
                val requestFile =file.asRequestBody("image/jpg".toMediaType())
                birthdayFileBody = MultipartBody.Part.createFormData(
                    "birthdayinfofile",
                    file.name,
                    requestFile
                )
            }

            if (model.rashanshopinfoBody.isNotEmpty()) {
                val file = File(model.rashanshopinfoBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                rashanshopinfoBody = MultipartBody.Part.createFormData(
                    "rashanshopinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.electricInfoFileBody.isNotEmpty()) {
                val file = File(model.electricInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                electricInfoFileBody = MultipartBody.Part.createFormData(
                    "electricityinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.electricInfoFileBody.isNotEmpty()) {
                val file = File(model.electricInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                drinkingwaterinfofileBody = MultipartBody.Part.createFormData(
                    "drinkingwaterinfofile",
                    file.name,
                    requestFile
                )
            }
            if (model.watercanelinfofileBody.isNotEmpty()) {
                val file = File(model.watercanelinfofileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                watercanelinfofileBody = MultipartBody.Part.createFormData(
                    "watercanelinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.schoolinfofileBody.isNotEmpty()) {
                val file = File(model.schoolinfofileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                schoolinfofileBody = MultipartBody.Part.createFormData(
                    "schoolinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.primaryHealthInfoFileBody.isNotEmpty()) {
                val file = File(model.primaryHealthInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                primaryHealthInfoFileBody = MultipartBody.Part.createFormData(
                    "primarycarecenterinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.vetarnityHealthInfoFileBody.isNotEmpty()) {
                val file = File(model.vetarnityHealthInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                vetarnityHealthInfoFileBody = MultipartBody.Part.createFormData(
                    "veterinarymedicineinfofile",
                    file.name,
                    requestFile
                )
            }

            if (model.govInfoInfoFileBody.isNotEmpty()) {
                val file = File(model.govInfoInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                govInfoInfoFileBody = MultipartBody.Part.createFormData(
                    "govservantinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.politicalInfoFileBody.isNotEmpty()) {
                val file = File(model.watercanelinfofileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                politicalInfoFileBody = MultipartBody.Part.createFormData(
                    "politicalinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.deathPersonInfoFileBody.isNotEmpty()) {
                val file = File(model.deathPersonInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                deathPersonInfoFileBody = MultipartBody.Part.createFormData(
                    "deathpersoninfofile",
                    file.name,
                    requestFile
                )
            }

            if (model.newschemesfileBody.isNotEmpty()) {
                val file = File(model.newschemesfileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                newschemesfileBody = MultipartBody.Part.createFormData(
                    "newschemesfile",
                    file.name,
                    requestFile
                )
            }

            if (model.devinfofileBody.isNotEmpty()) {
                val file = File(model.devinfofileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                devinfofileBody = MultipartBody.Part.createFormData(
                    "devinfofile",
                    file.name,
                    requestFile
                )
            }


            if (model.otherInfoFileBody.isNotEmpty()) {
                val file = File(model.otherInfoFileBody)
                val requestFile = file.asRequestBody("image/jpg".toMediaType())
                otherInfoFileBody = MultipartBody.Part.createFormData(
                    "otherinfofile",
                    file.name,
                    requestFile
                )
            }


                val response = service.createDailyVisit(
                    map, birthdayFileBody,
                    rashanshopinfoBody,
                    electricInfoFileBody,
                    drinkingwaterinfofileBody,
                    watercanelinfofileBody,
                    schoolinfofileBody,
                    primaryHealthInfoFileBody,
                    vetarnityHealthInfoFileBody,
                    govInfoInfoFileBody,
                    politicalInfoFileBody,
                    deathPersonInfoFileBody,
                    newschemesfileBody,
                    devinfofileBody,
                    otherInfoFileBody
                )
                if (response.isSuccessful) {
                    Cache.removeDailyVisit(model,pref)
                    Cache.dailyVisitOfflineList.remove(model)
                }
            }

        return Cache.dailyVisitOfflineList.isEmpty()
    }

    private suspend fun syncData(officeWorkOfflineList: ArrayList<OfficeWorkOfflineModel>):Boolean{

        officeWorkOfflineList.forEach {
            if (it.commentTxt.isNotEmpty()) {
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
                if (it.filePath.isNotEmpty()) {
                    val file = File(it.filePath)
                    val requestFile = file.asRequestBody("image/jpg".toMediaType())
                    body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                }
                val response = service.createWorkTask(map, body)
                if (response.isSuccessful && response.body() != null) {
                    Cache.officeWorkModelList.add(response.body()!!.data)
                    Cache.removeOfficeWork(it,pref)
                    Cache.officeWorkOfflineList.remove(it)
                }else{
                    return false
                }
            }

        }

        return Cache.officeWorkOfflineList.isEmpty()
    }
}

sealed class VillageState {
    class Failed(val msg: String) : VillageState()
    object Success : VillageState()
    object Empty : VillageState()
}


sealed class SyncState {
    class Failed(val msg: String) : SyncState()
    object Success : SyncState()
    object Processing : SyncState()
}