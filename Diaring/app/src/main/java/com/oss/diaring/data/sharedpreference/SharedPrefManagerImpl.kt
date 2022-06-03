package com.oss.diaring.data.sharedpreference

import android.content.Context
import com.oss.diaring.util.Constants.PREF_FILE_NAME

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
}