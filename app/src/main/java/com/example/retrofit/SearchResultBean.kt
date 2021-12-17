package com.example.retrofit


/**

 * @Author limeng
 * @Date 2020/8/10-4:36 PM
 */
data class SearchResultBean(
        var article: DataBean<Article>? = null,
)

class DataBean<T>{
        var count: String? = null
        var items: MutableList<T>? = null
        var has_more = false
}