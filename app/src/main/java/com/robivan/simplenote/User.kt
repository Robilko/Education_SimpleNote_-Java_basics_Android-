package com.robivan.simplenote

object User {
    var nameUser: String? = null
        private set
    var emailUser: String? = null
        private set
    private val userData: User = User
    fun getUserData(name: String?, email: String?): User {
        nameUser = name
        emailUser = email
        return userData
    }
}