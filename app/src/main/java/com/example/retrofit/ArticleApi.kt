package com.example.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ArticleApi {

    @GET("article")
    suspend fun searchArticles(
        @Query("key") key: String
    ): List<Article>


    //获取搜索资源结果 （发现页中的 幕课，音频，电子书等都是搜索接口）
    @GET("/weixin/official/search/")
    suspend fun getSearchResult(@QueryMap map: Map<String, String>): SearchResultBean
}
