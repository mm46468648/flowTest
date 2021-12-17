package com.example.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .client(OkHttpClient.Builder().build())
            .baseUrl("http://192.168.9.84:11112")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val articleApi: ArticleApi by lazy {
        instance.create(ArticleApi::class.java)
    }
}
