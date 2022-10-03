package com.vk.sarthi.cache

import com.vk.sarthi.model.*

object Cache {
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

    fun getDailyVisitModel(id: Int):DailyVisitModel? {
        return try {
            dailyVisitList.single { it.id == id }
        }catch(e :Exception) {
            null
        }
    }

    var villageData: VillageData? = null
}