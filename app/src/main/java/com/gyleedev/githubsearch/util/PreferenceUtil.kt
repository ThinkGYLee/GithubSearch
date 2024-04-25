package com.gyleedev.githubsearch.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

class PreferenceUtil(@ApplicationContext context: Context) {

    private val accessPreference: SharedPreferences =
        context.getSharedPreferences("AccessToken", Context.MODE_PRIVATE)

    fun getString(key: String = "Token", defValue: String): String {
        return accessPreference.getString(key, defValue) as String
    }

    fun setString(key: String = "Token", str: String) {
        accessPreference.edit().putString(key, str).apply()
    }
}