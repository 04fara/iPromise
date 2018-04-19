package com.ipromise

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.JsonObject
import com.ipromise.retrofit.ClientAPI
import kotlinx.android.synthetic.main.login_page.*

class LoginPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val clientAPI = ClientAPI()

        btnREGISTER.setOnClickListener {
            val json = JsonObject()
            json.addProperty("user_name", username.text.toString())
            json.addProperty("password", password.text.toString())
            clientAPI.register(this, json)
        }

        btnLOGIN.setOnClickListener {
            val preferences = MyPreferences(applicationContext)
            val json = JsonObject()
            json.addProperty("user_name", username.text.toString())
            json.addProperty("password", password.text.toString())
            preferences.setUsername(username.text.toString())
            clientAPI.login(this, json)
        }
    }
}
