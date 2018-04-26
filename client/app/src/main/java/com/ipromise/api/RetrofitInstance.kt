package com.ipromise.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object Factory {
        private const val BASE_URL = "http://10.0.2.2:5000"

        fun create(): RetrofitService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(RetrofitService::class.java)
        }
    }
}