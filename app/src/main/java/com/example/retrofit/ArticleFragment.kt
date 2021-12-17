package com.example.retrofit

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.simple.databinding.FragmentArticleBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class ArticleFragment : Fragment() {
    private val viewModel by viewModels<ArticleViewModel>()

    private val mBinding: FragmentArticleBinding by lazy {
        FragmentArticleBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    //获取关键字  分析1
    private fun TextView.textWatcherFlow(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                offer(s.toString())
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) }
    }

    // 定义一个全局的 StateFlow
    private val _etState = MutableStateFlow("")

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding.etSearch.doAfterTextChanged { text ->
            // 往流里写数据
            _etState.value = (text ?: "").toString()
        }
        lifecycleScope.launchWhenCreated {
            _etState.
            sample(500).filter {
                it.isNotEmpty()
            }.collect {
                viewModel.searchArticles(it)
            }
        }


        context?.let {
            val adapter = ArticleAdapter(null)
            mBinding.recyclerView.adapter = adapter
            //分析3
            viewModel.articles.observe(viewLifecycleOwner, { articles ->
                adapter.setData(articles)
            })
        }

    }

    class ArticleAdapter(var datas:List<Article>?) : RecyclerView.Adapter<MViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {

            return MViewHolder(createTextView(parent.context))
        }

        override fun onBindViewHolder(holder: MViewHolder, position: Int) {
            val user = datas?.get(position)
            holder.textView.text = "${user?.title}"

        }

        override fun getItemCount(): Int {
            return datas?.size?:0
        }

        fun setData(data: List<Article>){
            this.datas = data
            notifyDataSetChanged()
        }
        fun createTextView(c : Context) : TextView {
            val textView = TextView(c)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setSingleLine()
            textView.ellipsize = TextUtils.TruncateAt.END
            return textView
        }
    }

    class MViewHolder(view : View): RecyclerView.ViewHolder(view){
        lateinit var textView : TextView
        init {
            textView = view as TextView
        }
    }
}
