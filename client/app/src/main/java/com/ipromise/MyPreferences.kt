package com.ipromise

import android.content.Context

class MyPreferences(context: Context) {
    private val preference = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    private val editor = preference.edit()

    fun setToken(token: String) {
        editor.putString("Token", token)
        editor.apply()
    }

    fun getToken(): String {
        return preference.getString("Token", null)
    }

    fun setUsername(username: String) {
        editor.putString("Username", username)
        editor.apply()
    }

    fun getUsername(): String {
        return preference.getString("Username", null)
    }
}
