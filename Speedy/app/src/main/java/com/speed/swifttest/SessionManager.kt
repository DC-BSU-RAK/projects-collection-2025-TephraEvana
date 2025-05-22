package com.speed.swifttest;


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Class to manage user session data
 */
class SessionManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun setLoggedInUser(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }
    
    fun getLoggedInUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    
    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USERNAME)
        editor.apply()
    }
    
    fun saveUserExtendedInfo(username: String, extendedInfo: UserExtendedInfo) {
        val allExtendedInfo = getAllUserExtendedInfo().toMutableMap()
        allExtendedInfo[username] = extendedInfo
        
        val editor = sharedPreferences.edit()
        val json = gson.toJson(allExtendedInfo)
        editor.putString(KEY_EXTENDED_INFO, json)
        editor.apply()
    }
    
    fun getUserExtendedInfo(username: String): UserExtendedInfo? {
        val allExtendedInfo = getAllUserExtendedInfo()
        return allExtendedInfo[username]
    }
    
    private fun getAllUserExtendedInfo(): Map<String, UserExtendedInfo> {
        val json = sharedPreferences.getString(KEY_EXTENDED_INFO, null)
        if (json.isNullOrEmpty()) {
            return emptyMap()
        }
        val type = object : TypeToken<Map<String, UserExtendedInfo>>() {}.type
        return gson.fromJson(json, type)
    }
    
    companion object {
        private const val PREF_NAME = "session_prefs"
        private const val KEY_USERNAME = "logged_in_username"
        private const val KEY_EXTENDED_INFO = "user_extended_info"
        
        @Volatile
        private var instance: SessionManager? = null
        
        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

/**
 * Class to hold extended user information beyond the basic User model
 */
data class UserExtendedInfo(
    val age: String = "",
    val gender: String = "",
    val birthdate: String = "",
    val email: String = "",
    val address: String = "",
    val phone: String = ""
)