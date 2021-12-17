package com.example.room

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.simple.databinding.FragmentUserBinding
import kotlinx.coroutines.flow.collect


class UserFragment : Fragment() {

    private val viewModel by viewModels<UserViewModel>()

    //viewBinding固定代码
    private val mBinding: FragmentUserBinding by lazy {
        FragmentUserBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.apply {
            btnAddUser.setOnClickListener {
                viewModel.insert(
                    etUserId.text.toString(),
                    etFirstName.text.toString(),
                    etLastName.text.toString()
                )
            }
        }

        context?.let {
            val adapter = UserAdapter(null)
            mBinding.recyclerView.adapter = adapter
            lifecycleScope.launchWhenCreated {
                //通过collect 接收 getAll flow发射的数据
                viewModel.getAll().collect { value ->
                        adapter.setData(value)
                }
            }
        }
    }

    class UserAdapter(var datas:List<User>?) : RecyclerView.Adapter<MViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {

            return MViewHolder(createTextView(parent.context))
        }

        override fun onBindViewHolder(holder: MViewHolder, position: Int) {
            val user = datas?.get(position)
            holder.textView.text = "${user?.uid} -- ${user?.firstName} -- ${user?.lastName}"

        }

        override fun getItemCount(): Int {
            return datas?.size?:0
        }

        fun setData(data: List<User>){
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

    class MViewHolder(view : View):RecyclerView.ViewHolder(view){
        lateinit var textView : TextView
        init {
            textView = view as TextView
        }
    }


}
