package com.ipromise.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ipromise.R
import com.ipromise.api.models.PostModel

class PostAdapter(private val postList: ArrayList<PostModel>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = postList[position].user_name
        holder.title.text = postList[position].title
        holder.description.text = postList[position].description
        holder.deadline.text = postList[position].deadline
        holder.cardView.setCardBackgroundColor(R.drawable.tape)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.post_username)
        val title: TextView = itemView.findViewById(R.id.post_title)
        val description: TextView = itemView.findViewById(R.id.post_description)
        val deadline: TextView = itemView.findViewById(R.id.post_deadline)
        val cardView: CardView = itemView.findViewById(R.id.post_card)
    }
}