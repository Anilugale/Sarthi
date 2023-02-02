package com.vk.sarthi.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vk.sarthi.model.*
import com.vk.sarthi.utli.SettingPreferences
import com.vk.sarthi.viewmodel.OfficeWorkOfflineModel

object Cache {
    const val OFFICE_OFFLINE_LIST = "Office_Offline_List"
    val gson = Gson()
    fun getComplaintFromID(id: Int):ComplaintModel? {
        return try {
                  commentList.single { it.ticket_id == id }
        }catch(e :Exception) {
            null
        }
    }

    fun clear() {
         commentList.clear()
         officeWorkModelList.clear()
         loginUser=null
    }

    val commentList = arrayListOf<ComplaintModel>()
    val officeWorkModelList = arrayListOf<OfficeWorkModel>()
    var loginUser:UserModel?=null



    val dailyVisitList = arrayListOf<DailyVisitModel>()
    var villageMap = HashMap<Int,Village>()

    fun getDailyVisitModel(id: Int):DailyVisitModel? {
        return try {
            dailyVisitList.single { it.id == id }
        }catch(e :Exception) {
            null
        }
    }

    fun storeVillageData(current: Context) {
        val data = gson.toJson(villageData)
        val pref = SettingPreferences.get(current)
        pref.edit().putString("villageData",data).apply()
    }

    fun restoreVillageData(current: Context) {
        val pref = SettingPreferences.get(current)
        val data = pref.getString("villageData","")
        data?.let {
            villageData =gson.fromJson(it,VillageData::class.java)
        }
    }

    fun addOfficeWorkOffline(
        pref: SharedPreferences,
        officeWorkOfflineModel: OfficeWorkOfflineModel
    ) {
        val stringData = pref.getString(OFFICE_OFFLINE_LIST, null)
        var list = ArrayList<OfficeWorkOfflineModel>()
        if (stringData != null) {
            list = gson.fromJson(stringData, object :TypeToken<ArrayList<OfficeWorkOfflineModel>>(){}.type )
        }
        list.add(officeWorkOfflineModel)
        pref.edit().putString(OFFICE_OFFLINE_LIST,gson.toJson(list)).apply()
    }

    var officeWorkOfflineList = arrayListOf<OfficeWorkOfflineModel>()
    fun isOfflineOfficeWork(current:Context):Boolean{
         val pref = SettingPreferences.get(current)
         val stringData = pref.getString(OFFICE_OFFLINE_LIST, null)
        officeWorkOfflineList = ArrayList()
         if (stringData != null) {
             officeWorkOfflineList = gson.fromJson(stringData, object :TypeToken<ArrayList<OfficeWorkOfflineModel>>(){}.type )
         }
         return officeWorkOfflineList.isNotEmpty()
     }

    fun clearOfflineOfficeWork(current: Context) {
        val pref = SettingPreferences.get(current)
        val list = ArrayList<OfficeWorkOfflineModel>()
        pref.edit().putString(OFFICE_OFFLINE_LIST,gson.toJson(list)).apply()
    }

    var villageData: VillageData? = null
}