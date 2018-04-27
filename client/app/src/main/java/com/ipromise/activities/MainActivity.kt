package com.ipromise.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.ipromise.R
import com.ipromise.adapters.FragmentAdapter
import com.ipromise.fragments.FeedFragment
import com.ipromise.fragments.ProfileFragment
import com.ipromise.prefs.MyPreferences
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mSectionsPageAdapter: FragmentAdapter? = null

    private var mViewPager: ViewPager? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                setTitle(R.string.title_feed)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_add -> {
                val intent = Intent(this, AddPostActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_profile -> {
                setTitle(R.string.title_profile)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.title_feed)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSectionsPageAdapter = FragmentAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById<View>(R.id.viewpager) as ViewPager
        setupViewPager(mViewPager!!)

        navigation.onNavigationItemSelectedListener = mOnNavigationItemSelectedListener
        navigation.run {
            setTextVisibility(false)
            setupWithViewPager(mViewPager)
        }

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

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = FragmentAdapter(supportFragmentManager)
        adapter.addFragment(FeedFragment(), "Feed")
        adapter.addFragment(Fragment(), "Null")
        adapter.addFragment(ProfileFragment(), "My Profile")
        viewPager.adapter = adapter
    }
}
