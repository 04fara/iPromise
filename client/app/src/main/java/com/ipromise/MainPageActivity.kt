package com.ipromise

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.ipromise.retrofit.ClientAPI
import kotlinx.android.synthetic.main.main_page.*

class MainPageActivity : AppCompatActivity() {
    private val clientAPI = ClientAPI()
    private var users = ArrayList<User>()
    private var adapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)
        setSupportActionBar(toolbar)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = CustomAdapter(users, applicationContext)
        recyclerView.adapter = adapter
        clientAPI.fetchPosts(users, adapter!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val intent = Intent(this@MainPageActivity, LoginPageActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}