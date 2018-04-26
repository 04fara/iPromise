package com.ipromise.api

import android.app.Activity
import android.content.Intent
import com.google.gson.JsonObject
import com.ipromise.activities.MainActivity
import com.ipromise.adapters.PostAdapter
import com.ipromise.api.models.PostModel
import com.ipromise.api.models.ResponseTokenModel
import com.ipromise.api.models.UserModel
import com.ipromise.prefs.MyPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitController {
    private val service = RetrofitInstance.create()

    fun register(json: JsonObject) {
        service.register(json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            print(msg)
                        }
                    }
                })
    }

    fun login(activity: Activity, preferences: MyPreferences, json: JsonObject) {
        service.login(json)
                .enqueue(object : Callback<ResponseTokenModel> {
                    override fun onFailure(call: Call<ResponseTokenModel>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseTokenModel>, response: Response<ResponseTokenModel>) {
                        if (response.isSuccessful) preferences.setToken(response.body()?.access_token)

                        val intent = Intent(activity, MainActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    }
                })
    }

    fun follow(token: String, json: JsonObject) {
        service.followUser(token, json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            msg.equals("True")
                        }
                    }
                })
    }

    fun unfollow(token: String, json: JsonObject) {
        service.unfollowUser(token, json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            msg.equals("True")
                        }
                    }
                })
    }

    fun isFollowing(token: String, json: JsonObject) {
        service.isFollowingUser(token, json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            when {
                                msg.equals("True") -> 1
                                msg.equals("It is you") -> -1
                                else -> 0
                            }
                        }
                    }
                })
    }

    fun getFriendsList(token: String) {
        service.getFriendsList(token)
                .enqueue(object : Callback<List<UserModel>> {
                    override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                        if (response.isSuccessful) response.body() as ArrayList<UserModel>
                    }
                })
    }

    fun addPost(token: String, json: JsonObject) {
        service.addPost(token, json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        response.isSuccessful
                    }
                })
    }

    fun fetchPosts(token: String, posts: ArrayList<PostModel>, adapter: PostAdapter) {
        service.getPosts(token)
                .enqueue(object : Callback<List<PostModel>> {
                    override fun onFailure(call: Call<List<PostModel>>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<List<PostModel>>, response: Response<List<PostModel>>) {
                        if (response.isSuccessful) {
                            posts.clear()
                            posts.addAll(response.body() as ArrayList<PostModel>)
                            adapter.notifyDataSetChanged()
                        }
                    }
                })
    }
}