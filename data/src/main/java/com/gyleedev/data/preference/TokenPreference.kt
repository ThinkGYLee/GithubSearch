package com.gyleedev.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.gyleedev.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface TokenPreference {
    fun getString(): String
    fun setString(str: String)
    fun isKeyExist(): Boolean
    fun deleteKey()
}

// 구현체
class TokenPreferenceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : TokenPreference {
    private val prefFileName = context.getString(R.string.preference_file_key)
    private val prefTokenKey = context.getString(R.string.token_key)

    private val accessPref: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override fun getString(): String = accessPref.getString(prefTokenKey, "") as String

    override fun setString(str: String) {
        accessPref.edit { putString(prefTokenKey, str) }
    }

    // 키 값이 null/empty 가 아니면(존재하면) true
    override fun isKeyExist(): Boolean {
        val key = accessPref.getString(prefTokenKey, "")
        return !key.isNullOrEmpty()
    }

    override fun deleteKey() {
        setString(str = "")
    }
}
