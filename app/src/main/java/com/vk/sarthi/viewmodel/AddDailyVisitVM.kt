package com.vk.sarthi.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.service.Service
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
class AddDailyVisitVM @Inject constructor(val service: Service, private val pref: SharedPreferences) : ViewModel() {
    private var state: MutableStateFlow<DailyVisitState> = MutableStateFlow(DailyVisitState.Empty)
    val stateExpose = state.asStateFlow()
    private val personVisitedList = mutableStateListOf<PersonVisitedModel>()
    init {
        personVisitedList.add(PersonVisitedModel("1"))
    }

    fun setDailyVisitReq(
        visitid: Int,
        model: HashMap<String, String>,
        birthdayInfoFile: File?,
        rationInfoFile: File?,
        electricInfoFile:File?,
        drinkingInfoFile:File?,
        waterCanalInfoFile:File?,
        schoolInfoFile:File?,
        primaryHealthInfoFile:File?,
        vetarnityHealthInfoFile:File?,
        govInfoInfoFile:File?,
        politicalInfoFile:File?,
        deathPersonInfoFile:File?,
        yojnaInfoFile:File?,
        devFile:File?,
        otherInfoFile:File?,
        village: String
    ) {


        if (WifiService.instance.isOnline()) {
            state.value = DailyVisitState.Process
            val map = HashMap<String, RequestBody>()
            model.forEach {
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



            if (birthdayInfoFile != null) {
                val requestFile =
                    birthdayInfoFile.asRequestBody("image/jpg".toMediaType())
                birthdayFileBody = MultipartBody.Part.createFormData(
                    "birthdayinfofile",
                    birthdayInfoFile.name,
                    requestFile
                )
            }

            if (rationInfoFile != null) {
                val requestFile = rationInfoFile.asRequestBody("image/jpg".toMediaType())
                rashanshopinfoBody = MultipartBody.Part.createFormData(
                    "rashanshopinfofile",
                    rationInfoFile.name,
                    requestFile
                )
            }


            if (electricInfoFile != null) {
                val requestFile =
                    electricInfoFile.asRequestBody("image/jpg".toMediaType())
                electricInfoFileBody = MultipartBody.Part.createFormData(
                    "electricityinfofile",
                    electricInfoFile.name,
                    requestFile
                )
            }


            if (drinkingInfoFile != null) {
                val requestFile =
                    drinkingInfoFile.asRequestBody("image/jpg".toMediaType())
                drinkingwaterinfofileBody = MultipartBody.Part.createFormData(
                    "drinkingwaterinfofile",
                    drinkingInfoFile.name,
                    requestFile
                )
            }

            if (waterCanalInfoFile != null) {
                val requestFile =
                    waterCanalInfoFile.asRequestBody("image/jpg".toMediaType())
                watercanelinfofileBody = MultipartBody.Part.createFormData(
                    "watercanelinfofile",
                    waterCanalInfoFile.name,
                    requestFile
                )
            }


            if (schoolInfoFile != null) {
                val requestFile = schoolInfoFile.asRequestBody("image/jpg".toMediaType())
                schoolinfofileBody = MultipartBody.Part.createFormData(
                    "schoolinfofile",
                    schoolInfoFile.name,
                    requestFile
                )
            }


            if (primaryHealthInfoFile != null) {
                val requestFile =
                    primaryHealthInfoFile.asRequestBody("image/jpg".toMediaType())
                primaryHealthInfoFileBody = MultipartBody.Part.createFormData(
                    "primarycarecenterinfofile",
                    primaryHealthInfoFile.name,
                    requestFile
                )
            }


            if (vetarnityHealthInfoFile != null) {
                val requestFile =
                    vetarnityHealthInfoFile.asRequestBody("image/jpg".toMediaType())
                vetarnityHealthInfoFileBody = MultipartBody.Part.createFormData(
                    "veterinarymedicineinfofile",
                    vetarnityHealthInfoFile.name,
                    requestFile
                )
            }

            if (govInfoInfoFile != null) {
                val requestFile = govInfoInfoFile.asRequestBody("image/jpg".toMediaType())
                govInfoInfoFileBody = MultipartBody.Part.createFormData(
                    "govservantinfofile",
                    govInfoInfoFile.name,
                    requestFile
                )
            }


            if (politicalInfoFile != null) {
                val requestFile =
                    politicalInfoFile.asRequestBody("image/jpg".toMediaType())
                politicalInfoFileBody = MultipartBody.Part.createFormData(
                    "politicalinfofile",
                    politicalInfoFile.name,
                    requestFile
                )
            }


            if (deathPersonInfoFile != null) {
                val requestFile =
                    deathPersonInfoFile.asRequestBody("image/jpg".toMediaType())
                deathPersonInfoFileBody = MultipartBody.Part.createFormData(
                    "deathpersoninfofile",
                    deathPersonInfoFile.name,
                    requestFile
                )
            }

            if (yojnaInfoFile != null) {
                val requestFile = yojnaInfoFile.asRequestBody("image/jpg".toMediaType())
                newschemesfileBody = MultipartBody.Part.createFormData(
                    "newschemesfile",
                    yojnaInfoFile.name,
                    requestFile
                )
            }

            if (devFile != null) {
                val requestFile = devFile.asRequestBody("image/jpg".toMediaType())
                devinfofileBody = MultipartBody.Part.createFormData(
                    "devinfofile",
                    devFile.name,
                    requestFile
                )
            }


            if (otherInfoFile != null) {
                val requestFile = otherInfoFile.asRequestBody("image/jpg".toMediaType())
                otherInfoFileBody = MultipartBody.Part.createFormData(
                    "otherinfofile",
                    otherInfoFile.name,
                    requestFile
                )
            }

            viewModelScope.launch {
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
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (visitid != 0) {
                                Cache.dailyVisitList.removeIf { it.id == visitid }
                            }
                            response.body()!!.data.villagename = village
                            Cache.dailyVisitList.add(response.body()!!.data)
                            state.value = DailyVisitState.Success(response.body()!!.messages)
                        } else {
                            state.value = DailyVisitState.Failed(response.message())
                        }
                    } else {
                        state.value = DailyVisitState.Failed(Constants.Error)
                    }
                }
            }
        }else{
            Cache.addDailyVisitOffline(
                pref = pref,
                model = VisitOffLineModel(
                    id = System.currentTimeMillis().toString(),
                    model,
                    birthdayFileBody = if (birthdayInfoFile!=null) { birthdayInfoFile.path}else{""},
                    rashanshopinfoBody = if (rationInfoFile!=null) { rationInfoFile.path}else{""},
                    electricInfoFileBody = if (electricInfoFile!=null) { electricInfoFile.path}else{""},
                    drinkingwaterinfofileBody = if (drinkingInfoFile!=null) { drinkingInfoFile.path}else{""},
                    watercanelinfofileBody = if (waterCanalInfoFile!=null) { waterCanalInfoFile.path}else{""},
                    schoolinfofileBody = if (schoolInfoFile!=null) { schoolInfoFile.path}else{""},
                    primaryHealthInfoFileBody = if (primaryHealthInfoFile!=null) { primaryHealthInfoFile.path}else{""},
                    vetarnityHealthInfoFileBody = if (vetarnityHealthInfoFile!=null) { vetarnityHealthInfoFile.path}else{""},
                    govInfoInfoFileBody = if (govInfoInfoFile!=null) { govInfoInfoFile.path}else{""},
                    politicalInfoFileBody = if (politicalInfoFile!=null) { politicalInfoFile.path}else{""},
                    deathPersonInfoFileBody = if (deathPersonInfoFile!=null) { deathPersonInfoFile.path}else{""},
                    newschemesfileBody = if (yojnaInfoFile!=null) { yojnaInfoFile.path}else{""},
                    devinfofileBody = if (devFile!=null) { devFile.path}else{""},
                    otherInfoFileBody = if (otherInfoFile!=null) { otherInfoFile.path}else{""},
                    villageName = village
                )
            )
            state.value = DailyVisitState.Success(Constants.SAVE_OFFLINE)
        }
    }



}
data class VisitOffLineModel(
    var id :String,
    val hashMap: HashMap<String, String>,
    val birthdayFileBody: String,
    val  rashanshopinfoBody: String,
    val electricInfoFileBody: String,
    val drinkingwaterinfofileBody: String,
    val watercanelinfofileBody: String,
    val schoolinfofileBody: String,
    val primaryHealthInfoFileBody: String,
    val vetarnityHealthInfoFileBody: String,
    val govInfoInfoFileBody: String,
    val politicalInfoFileBody: String,
    val deathPersonInfoFileBody: String,
    val newschemesfileBody: String,
    val devinfofileBody: String,
    val otherInfoFileBody: String,
    val villageName:String
)
sealed class DailyVisitState {
    class Success(val msg: String) : DailyVisitState()
    object Process : DailyVisitState()
    object Empty : DailyVisitState()
    class Failed(val msg:String) : DailyVisitState()
}


class PersonVisitedModel(val id: String) {
    var name = mutableStateOf("")
    var subject = mutableStateOf("")
    var information = mutableStateOf("")
    var survey = mutableStateOf("")

    var isN = mutableStateOf(false)
    var isS = mutableStateOf(false)
    var isIn = mutableStateOf(false)
    var isSu = mutableStateOf(false)
}