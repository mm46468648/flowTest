package com.example.retrofit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ArticleViewModel(app: Application) : AndroidViewModel(app) {

    //定义对应LiveData数据类型
    val articles = MutableLiveData<List<Article>>()


    fun searchArticles(key: String) {
        viewModelScope.launch {
            flow {
                //这里就是通过Retrofit从服务器拿到对应key过滤后的文章内容
                val map = mapOf("word" to key, "search_match_type" to 1.toString(), "page_size" to 30)
                val list = RetrofitClient.articleApi.getSearchResult(map as Map<String, String>)
                //将对应数据发射出去
                emit(list)
            }.flowOn(Dispatchers.IO)
                .catch {
                        e -> e.printStackTrace()
                }
                .collect {
                    //这里收到对应数据，更新对应的LiveData数据
                    articles.setValue(it.article?.items)
                }
        }
    }

}
