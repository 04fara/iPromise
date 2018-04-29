package com.ipromise.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import com.ipromise.R
import com.ipromise.adapters.UserAdapter
import com.ipromise.api.RetrofitController
import com.ipromise.api.models.UserModel
import com.ipromise.prefs.MyPreferences
import com.ipromise.utils.JSONBuilder

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val users = ArrayList<UserModel>()
        val searchField = findViewById<EditText>(R.id.search_field)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val adapter = UserAdapter(users, this)
        recyclerView.adapter = adapter
        searchField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val json = JSONBuilder()
                        .append("regexp", searchField.text.toString())
                        .build()
                RetrofitController().searchUsers(this@SearchActivity, MyPreferences(applicationContext).getToken(), json, users, adapter)
            }
        })
    }
}