package com.ipromise.api

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import com.google.gson.JsonObject
import com.ipromise.activities.MainActivity
import com.ipromise.adapters.PostAdapter
import com.ipromise.adapters.UserAdapter
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

    fun follow(token: String, json: JsonObject, button: Button) {
        service.followUser(token, json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            msg.equals("True")
                            button.text = "Following"
                        }
                    }
                })
    }

    fun unfollow(token: String, json: JsonObject, button: Button) {
        service.unfollowUser(token, json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            msg.equals("True")
                            button.text = "Follow"
                        }
                    }
                })
    }

    fun isFollowing(token: String, json: JsonObject, button: Button) {
        service.isFollowingUser(token, json)
                .enqueue(object : Callback<ResponseTokenModel> {
                    override fun onFailure(call: Call<ResponseTokenModel>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseTokenModel>, response: Response<ResponseTokenModel>) {
                        if (response.isSuccessful) {
                            val msg = response.body()!!.message
                            print(msg)
                            button.text = when {
                                msg.equals("true") -> "Following"
                                msg.equals("It is you") -> "It is you"
                                else -> "Follow"
                            }
                        }
                    }
                })
    }

    fun getUserInfo(token: String, username: TextView?) {
        service.getUserInfo(token)
                .enqueue(object : Callback<UserModel> {
                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) username!!.text = response.body()!!.user_name
                    }
                })
    }

    fun searchUsers(token: String, json: JsonObject, users: ArrayList<UserModel>, adapter: UserAdapter) {
        service.searchUsers(token, json)
                .enqueue(object : Callback<List<UserModel>> {
                    override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                        if (response.isSuccessful) {
                            users.clear()
                            users.addAll(response.body() as ArrayList<UserModel>)
                            adapter.notifyDataSetChanged()
                        }
                    }
                })
    }

    fun getFollowersList(token: String, users: ArrayList<UserModel>, adapter: UserAdapter) {
        service.getFollowersList(token)
                .enqueue(object : Callback<List<UserModel>> {
                    override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                        if (response.isSuccessful) {
                            users.clear()
                            users.addAll(response.body() as ArrayList<UserModel>)
                            adapter.notifyDataSetChanged()
                        }
                    }
                })
    }

    fun getFollowedList(token: String, users: ArrayList<UserModel>, adapter: UserAdapter) {
        service.getFollowedList(token)
                .enqueue(object : Callback<List<UserModel>> {
                    override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                        if (response.isSuccessful) {
                            users.clear()
                            users.addAll(response.body() as ArrayList<UserModel>)
                            adapter.notifyDataSetChanged()
                        }
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

    fun fetchPosts(token: String, json: JsonObject, posts: ArrayList<PostModel>, adapter: PostAdapter) {
        service.getPosts(token, json)
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