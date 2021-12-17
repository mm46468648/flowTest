package com.example.download

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.simple.databinding.FragmentDownloadBinding
import kotlinx.coroutines.flow.collect
import java.io.File

class DownloadFragment : Fragment() {

    val URL = "http://static.moocnd.ykt.io/moocnd/img/802aae964bfc8b8c54eba305f6cfc03d.jpeg"

    //初始化ViewBinding代码块固定代码
    private val mBinding: FragmentDownloadBinding by lazy {
        FragmentDownloadBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //布局使用ViewBinding对应的root
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            context?.apply {
                val file = File(getExternalFilesDir(null)?.path, "pic.JPG")
                DownloadManager.download(URL,file).collect { status->
                    when (status) {
                        is DownloadStatus.Progress -> {
                            mBinding.apply {
                                progressBar.progress = status.value
                                tvProgress.text = "${status.value}%"
                            }
                        }
                        is DownloadStatus.Error -> {
                            Toast.makeText(context, "下载错误", Toast.LENGTH_SHORT).show()
                        }
                        is DownloadStatus.Done -> {
                            mBinding.apply {
                                progressBar.progress = 100
                                tvProgress.text = "100%"
                            }
                            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.d("ning", "下载失败.")
                        }
                    }

                }
            }
        }
    }

}
