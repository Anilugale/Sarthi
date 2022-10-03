package com.vk.sarthi.utli

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.UserModel

object SettingPreferences {
    var sharedPreferences:SharedPreferences?=null
    private val gson = Gson()

    fun get(context: Context):SharedPreferences {
        if (sharedPreferences == null) {
            sharedPreferences =
                context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        }

        return sharedPreferences!!
    }
    fun saveUser(user: UserModel,context: Context) {
        get(context)
        Cache.loginUser = user
        sharedPreferences?.apply {
            edit().putString(USER,gson.toJson(user)).apply()
        }
    }

    fun getFCMToken(context: Context):String {
        get(context)
        var token = ""
        sharedPreferences?.apply {
            token = getString(TOKEN,"").toString()
        }
        return token
    }

    fun isUserSave(context: Context): Boolean {
        get(context)
        if (sharedPreferences != null) {
            val userStr = sharedPreferences?.getString(USER, null)
            try {
                Cache.loginUser = gson.fromJson(userStr,UserModel::class.java)
                if (Cache.loginUser != null) {
                    return true
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }

        return false
    }

    fun clearCache(context: Context) {
        get(context)
        sharedPreferences?.apply {
            edit().clear().commit()
        }
    }

    private const val USER = "USER"
    const val TOKEN = "TOKEN"

}