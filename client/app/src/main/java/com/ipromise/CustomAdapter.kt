package com.ipromise

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.gson.JsonObject
import com.ipromise.retrofit.ClientAPI

class CustomAdapter(private val userList: ArrayList<User>, private val applicationContext: Context) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val preferences = MyPreferences(applicationContext)
        val clientAPI = ClientAPI()

        holder.txtName.text = userList[position].user_id.toString()
        holder.txtTitle.text = userList[position].user_name

        val button = holder.txtButton

        val json = JsonObject()
        json.addProperty("follower", preferences.getUsername())
        json.addProperty("followed", holder.txtTitle.text.toString())
        clientAPI.isFollowing(button, json)

        button.setOnClickListener({
            if (button.text.toString() == "Follow") clientAPI.follow(button, json)
            else clientAPI.unfollow(button, json)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtButton = itemView.findViewById<Button>(R.id.txtButton)!!
        val txtName = itemView.findViewById<TextView>(R.id.txtUsername)!!
        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)!!
    }

}