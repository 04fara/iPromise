package com.ipromise.retrofit

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.ipromise.CustomAdapter
import com.ipromise.MainPageActivity
import com.ipromise.ResponseToken
import com.ipromise.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


class ClientAPI {
    companion object {
        private val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

        var service = retrofit
                .create(APIService::class.java)!!
    }

    interface APIService {
        @POST("/register")
        fun register(@Body body: JsonObject): Call<ResponseBody>

        @POST("/login")
        fun login(@Body body: JsonObject): Call<ResponseToken>

        @POST("/friends")
        fun friends(): Call<List<User>>

        @POST("/follow")
        fun followUser(@Body body: JsonObject): Call<ResponseBody>

        @POST("/unfollow")
        fun unfollowUser(@Body body: JsonObject): Call<ResponseBody>

        @POST("/is_following")
        fun isFollowingUser(@Body body: JsonObject): Call<ResponseBody>
    }

    fun register(activity: Activity, json: JsonObject) {
        ClientAPI
                .service
                .register(json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            Toast.makeText(activity.applicationContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

    fun login(activity: Activity, json: JsonObject) {
        ClientAPI
                .service
                .login(json)
                .enqueue(object : Callback<ResponseToken> {
                    override fun onFailure(call: Call<ResponseToken>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseToken>, response: Response<ResponseToken>) {
                        if (response.isSuccessful) {
                            val msg = response.body()
                            Toast.makeText(activity.applicationContext, msg?.message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MainPageActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }
                    }
                })
    }

    fun follow(button: Button, json: JsonObject) {
        ClientAPI
                .service
                .followUser(json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            if (msg.equals("True")) button.text = "Following"
                        }
                    }
                })
    }

    fun unfollow(button: Button, json: JsonObject) {
        ClientAPI
                .service
                .unfollowUser(json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            if (msg.equals("True")) button.text = "Follow"
                        }
                    }
                })
    }

    fun isFollowing(button: Button, json: JsonObject) {
        ClientAPI
                .service
                .isFollowingUser(json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val msg = response.body()?.string()
                            when {
                                msg.equals("True") -> button.text = "Following"
                                msg.equals("It is you") -> {
                                    button.isEnabled = false
                                    button.text = "It is you"
                                }
                                else -> button.text = "Follow"
                            }
                        }
                    }
                })
    }

    fun fetchPosts(users: ArrayList<User>, adapter: CustomAdapter) {
        ClientAPI
                .service
                .friends()
                .enqueue(object : Callback<List<User>> {
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        println("EXCEPTION: " + t.message)
                    }

                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        if (response.isSuccessful) {
                            val items: List<User> = response.body() as ArrayList<User>
                            println(response.body())
                            users.clear()
                            users.addAll(items)
                            adapter.notifyDataSetChanged()
                        }
                    }
                })
    }
}