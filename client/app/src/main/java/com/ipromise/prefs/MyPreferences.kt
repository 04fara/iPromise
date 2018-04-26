package com.ipromise.prefs

import android.content.Context

class MyPreferences(context: Context) {
    private val preference = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
    private val editor = preference.edit()

    fun setToken(token: String?) {
        editor.putString("token", "Bearer $token")
        editor.apply()
    }

    fun getToken(): String {
        return preference.getString("token", null)
    }
}