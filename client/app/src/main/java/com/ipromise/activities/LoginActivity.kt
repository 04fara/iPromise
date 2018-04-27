package com.ipromise.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ipromise.R
import com.ipromise.api.RetrofitController
import com.ipromise.prefs.MyPreferences
import com.ipromise.utils.JSONBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val retrofitController = RetrofitController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun register(view: View) {
        val json = JSONBuilder().append("user_name", username.text.toString())
                .append("password", password.text.toString())
                .build()
        retrofitController.register(this, json)
    }

    fun login(view: View) {
        val json = JSONBuilder().append("user_name", username.text.toString())
                .append("password", password.text.toString())
                .build()
        retrofitController.login(this, MyPreferences(applicationContext), json)
    }
}