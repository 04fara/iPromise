package com.ipromise.activities

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.ipromise.R
import com.ipromise.fragments.FeedFragment
import com.ipromise.fragments.ProfileFragment
import com.ipromise.prefs.MyPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var fragmentList: ArrayList<Fragment>? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                setTitle(R.string.title_feed)
                replaceFragment(fragmentList!![0])
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_add -> {
                val intent = Intent(this, AddPostActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_profile -> {
                setTitle(R.string.title_profile)
                replaceFragment(fragmentList!![1])
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.title_feed)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFragmentList()
        replaceFragment(fragmentList!![0])

        navigation.onNavigationItemSelectedListener = mOnNavigationItemSelectedListener
        navigation.setTextVisibility(false)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_incoming -> {
                val intent = Intent(this, IncomingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                MyPreferences(this).setToken("")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.viewpager_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setupFragmentList() {
        fragmentList = ArrayList()
        fragmentList!!.add(FeedFragment())
        fragmentList!!.add(ProfileFragment())
    }
}
