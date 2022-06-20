package com.oss.diaring.data.sharedpreference

interface SharedPrefManager {

    fun setNickName(key: String, value: String)

    fun getNickName(key: String): String?

    fun setUserId(key: String, value: String)

    fun getUserId(key: String): String?

    fun setEmail(key: String, value: String)

    fun getEmail(key: String): String?

    fun setIsLoginFirst(key: String, value: Boolean)

    fun getIsLoginFirst(key: String): Boolean
}