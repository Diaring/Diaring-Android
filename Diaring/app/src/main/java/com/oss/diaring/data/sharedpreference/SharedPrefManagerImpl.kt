package com.oss.diaring.data.sharedpreference

import android.content.Context
import com.oss.diaring.util.Constants.PREF_FILE_NAME

// 간단한 값 저장을 위한 Preference 인터페이스 구현체
class SharedPrefManagerImpl(private val context: Context) : SharedPrefManager {

    private val preference = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    override fun setNickName(key: String, value: String) {
        preference.edit()
            .putString(key, value)
            .apply()
    }

    override fun getNickName(key: String): String? {
        return preference.getString(key, "닉네임이 없어요.")
    }

    override fun setUserId(key: String, value: String) {
        preference.edit()
            .putString(key, value)
            .apply()
    }

    override fun getUserId(key: String): String? {
        return preference.getString(key, "")
    }

    override fun setEmail(key: String, value: String) {
        preference.edit()
            .putString(key, value)
            .apply()
    }

    override fun getEmail(key: String): String? {
        return preference.getString(key, "")
    }

    override fun setIsLoginFirst(key: String, value: Boolean) {
        preference.edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun getIsLoginFirst(key: String): Boolean {
        return preference.getBoolean(key, true)
    }
}