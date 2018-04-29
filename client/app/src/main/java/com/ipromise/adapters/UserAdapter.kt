package com.ipromise.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ipromise.R
import com.ipromise.api.RetrofitController
import com.ipromise.api.models.UserModel
import com.ipromise.prefs.MyPreferences
import com.ipromise.utils.JSONBuilder

class UserAdapter(private val userList: ArrayList<UserModel>,
                  private val activity: Activity) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val token = MyPreferences(activity.applicationContext).getToken()
        holder.username.text = userList[position].user_name
        val button = holder.button
        val json = JSONBuilder().append("other_user", holder.username.text as String).build()
        RetrofitController().isFollowing(activity, token, json, button)
        button.setOnClickListener({
            if (button.text.toString() == "Follow") RetrofitController().follow(activity, token, json, button)
            else RetrofitController().unfollow(activity, token, json, button)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_user, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById<TextView>(R.id.username)!!
        val button = itemView.findViewById<Button>(R.id.follow_button)!!
    }
}