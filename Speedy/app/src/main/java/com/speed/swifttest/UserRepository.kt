package com.speed.swifttest;

import android.content.Context
class UserRepository private constructor(private val context: Context) {
    private val sharedPrefManager = SharedPrefManager.getInstance(context)
    private var users = mutableListOf<User>()

    init {
        // Load saved users
        users.addAll(sharedPrefManager.getUsers())
    }
// Add these methods to the UserRepository class:

fun getUsers(): List<User> {
    return users.toList()
}

fun saveUsers(updatedUsers: List<User>) {
    users.clear()
    users.addAll(updatedUsers)
    sharedPrefManager.saveUsers(users)
}

    fun registerUser(user: User): Boolean {
        if (getUserByUsername(user.username) != null) {
            return false // Username already exists
        }
        users.add(user)
        // Save updated users list
        sharedPrefManager.saveUsers(users)
        return true
    }

    fun authenticateUser(username: String, password: String): Boolean {
        val user = getUserByUsername(username)
        return user?.password == password
    }

    fun getUserByUsername(username: String): User? {
        return users.find { it.username == username }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}