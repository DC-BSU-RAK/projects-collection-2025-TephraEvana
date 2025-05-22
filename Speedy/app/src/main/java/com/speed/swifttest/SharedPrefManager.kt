package com.speed.swifttest;

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUsers(users: List<User>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(users)
        editor.putString(KEY_USERS, json)
        editor.apply()
    }

    fun getUsers(): List<User> {
        val json = sharedPreferences.getString(KEY_USERS, null)
        if (json.isNullOrEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(json, type)
    }

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_USERS = "users"

        @Volatile
        private var instance: SharedPrefManager? = null

        fun getInstance(context: Context): SharedPrefManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefManager(context.applicationContext).also { instance = it }
            }
        }
    }
}