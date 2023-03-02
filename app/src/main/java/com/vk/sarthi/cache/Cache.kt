package com.vk.sarthi.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vk.sarthi.model.*
import com.vk.sarthi.utli.SettingPreferences
import com.vk.sarthi.viewmodel.OfficeWorkOfflineModel
import com.vk.sarthi.viewmodel.VisitOffLineModel

object Cache {
    private const val OFFICE_OFFLINE_LIST = "Office_Offline_List"
    private const val DAILY_OFFLINE_LIST = "Daily_Offline_List"
    private val gson = Gson()
    var villageData: VillageData? = null

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
    fun removeOfficeWork(removeItems: OfficeWorkOfflineModel, pref: SharedPreferences) {
        val stringData = pref.getString(OFFICE_OFFLINE_LIST, null)
        var officeWorkOfflineList = ArrayList<OfficeWorkOfflineModel>()
        if (stringData != null) {
            officeWorkOfflineList = gson.fromJson(stringData, object :TypeToken<ArrayList<OfficeWorkOfflineModel>>(){}.type )
        }
        officeWorkOfflineList.remove(removeItems)
        pref.edit().putString(OFFICE_OFFLINE_LIST,gson.toJson(officeWorkOfflineList)).apply()
    }

    var officeWorkOfflineList = arrayListOf<OfficeWorkOfflineModel>()
    fun isOfflineOfficeWork(current:Context):Boolean{
         val pref = SettingPreferences.get(current)
         val stringData = pref.getString(OFFICE_OFFLINE_LIST, null)
        officeWorkOfflineList = ArrayList()
         if (stringData != null) {
             officeWorkOfflineList = gson.fromJson(stringData, object :TypeToken<ArrayList<OfficeWorkOfflineModel>>(){}.type )
         }

        if (officeWorkOfflineList.isNotEmpty() && officeWorkOfflineList[0] == null) {
            clearOfflineOfficeWork(current)
            with(officeWorkOfflineList) {
                forEachIndexed { index, it ->
                    if (it.id == null) {
                        it.id = System.currentTimeMillis().toString() + index
                    }
                    addOfficeWorkOffline(pref, it)
                }
            }
        }
         return officeWorkOfflineList.isNotEmpty()
     }


    fun updatedOffLineModel(dataModel: OfficeWorkOfflineModel, context: Context) {
        val pref = SettingPreferences.get(context)
        clearOfflineOfficeWork(context)
        with(officeWorkOfflineList) {
            forEach {
                if (it.id == dataModel.id) {
                    it.commentTxt = dataModel.commentTxt
                    it.filePath = dataModel.filePath
                    addOfficeWorkOffline(pref, dataModel)
                }else {
                    addOfficeWorkOffline(pref, it)
                }
            }
        }
    }

    fun clearOfflineOfficeWork(current: Context) {
        val pref = SettingPreferences.get(current)
        val list = ArrayList<OfficeWorkOfflineModel>()
        pref.edit().putString(OFFICE_OFFLINE_LIST,gson.toJson(list)).apply()
    }


    var dailyVisitOfflineList = arrayListOf<VisitOffLineModel>()
    fun addDailyVisitOffline(
        pref: SharedPreferences,
        model: VisitOffLineModel
    ) {
        val stringData = pref.getString(DAILY_OFFLINE_LIST, null)
        var list = ArrayList<VisitOffLineModel>()
        if (stringData != null) {
            list = gson.fromJson(stringData, object :TypeToken<ArrayList<VisitOffLineModel>>(){}.type )
        }
        list.add(model)
        pref.edit().putString(DAILY_OFFLINE_LIST,gson.toJson(list)).apply()
    }


    fun isOfflineDailyVisit(current:Context):Boolean{
        val pref = SettingPreferences.get(current)
        val stringData = pref.getString(DAILY_OFFLINE_LIST, null)
        dailyVisitOfflineList = ArrayList()
        if (stringData != null) {
            dailyVisitOfflineList = gson.fromJson(stringData, object :TypeToken<ArrayList<VisitOffLineModel>>(){}.type )
        }
        return dailyVisitOfflineList.isNotEmpty()
    }

    fun clearOfflineODailyVisit(current: Context) {
        val pref = SettingPreferences.get(current)
        val list = ArrayList<VisitOffLineModel>()
        pref.edit().putString(DAILY_OFFLINE_LIST,gson.toJson(list)).apply()
    }
    fun removeDailyVisit(removeItems: VisitOffLineModel, pref: SharedPreferences) {
        val stringData = pref.getString(DAILY_OFFLINE_LIST, null)
        var dailyVisitOfflineList = ArrayList<VisitOffLineModel>()
        if (stringData != null) {
            dailyVisitOfflineList = gson.fromJson(stringData, object :TypeToken<ArrayList<VisitOffLineModel>>(){}.type )
        }
        dailyVisitOfflineList.remove(removeItems)
        pref.edit().putString(DAILY_OFFLINE_LIST,gson.toJson(dailyVisitOfflineList)).apply()
    }



}