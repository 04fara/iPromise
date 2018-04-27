package com.ipromise.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ipromise.R
import com.ipromise.api.RetrofitController
import com.ipromise.prefs.MyPreferences
import com.ipromise.utils.JSONBuilder
import kotlinx.android.synthetic.main.activity_add_post.*
import java.util.*


class AddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
    }

    fun setDate(view: View) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val picker = DatePickerDialog(this@AddPostActivity,
                DatePickerDialog.OnDateSetListener { _, yearChosen, monthOfYear, dayOfMonth -> goal_deadline.setText("$yearChosen-${monthOfYear + 1}-$dayOfMonth") }, year, month, day)
        picker.show()
    }

    fun submit(view: View) {
        val json = JSONBuilder().append("title", goal_title.text.toString())
                .append("description", goal_description.text.toString())
                .append("deadline", goal_deadline.text.toString())
                .build()
        RetrofitController().addPost(this, MyPreferences(applicationContext).getToken(), json)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}