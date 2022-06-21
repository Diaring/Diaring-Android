package com.oss.diaring.data.sharedpreference

// 간단한 값 저장을 위한 Preference 인터페이스
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