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
    private var loginPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun register(view: View) {
        when (loginPage) {
            true -> {
                button.text = "Register"
                register.text = "Login"
            }
            else -> {
                button.text = "Login"
                register.text = "Register"
            }
        }
        username.text.clear()
        password.text.clear()
        loginPage = !loginPage
    }

    fun onClick(view: View) {
        val json = JSONBuilder().append("user_name", username.text.toString())
                .append("password", password.text.toString())
                .build()
        if (loginPage) retrofitController.login(this, MyPreferences(applicationContext), json)
        else retrofitController.register(this, view, json)
    }
}