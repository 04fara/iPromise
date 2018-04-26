package com.ipromise.api.models

data class PostModel(
        val post_id: Int,
        val user_name: String,
        val title: String,
        val description: String,
        val timestmp: String,
        val deadline: String
)