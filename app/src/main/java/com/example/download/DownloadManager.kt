package com.example.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

object DownloadManager {
    /**
     * 文件下载
     * @url 下载路径
     * @file 本地保存文件
     */
    fun download(url: String, file: File): Flow<DownloadStatus> {

        return flow {
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient.Builder().build().newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.let { body ->
                    val total = body.contentLength()
                    //文件读写
                    file.outputStream().use { output ->
                        val input = body.byteStream()
                        var emittedProgress = 0L
                        //使用对应的扩展函数 ，因为该函数最后参为内联函数，因此需要在后面实现对应业务逻辑
                        input.copyTo(output) { bytesCopied ->
                            //获取下载进度百分比
                            val progress = bytesCopied * 100 / total
                            //每下载进度比上次大于5时，通知UI线程
                            if (progress - emittedProgress > 5) {
                                delay(100)
                                //使用Flow对应的emit 发送对应下载进度通知
                                emit(DownloadStatus.Progress(progress.toInt()))
                                //记录当前下载进度
                                emittedProgress = progress
                            }
                        }
                    }
                }
                //发送下载完成通知
                emit(DownloadStatus.Done(file))
            } else {
                throw IOException(response.toString())
            }
        }.catch {
            //下载失败，删除该文件，并发送失败通知
            file.delete()
            emit(DownloadStatus.Error(it))
        }.flowOn(Dispatchers.IO) //因为下载文件是属于异步IO操作，因此这里改变上下文

    }

}
sealed class DownloadStatus {
    object None : DownloadStatus() //空状态
    data class Progress(val value: Int) : DownloadStatus() //下载进度
    data class Error(val throwable: Throwable) : DownloadStatus() //错误
    data class Done(val file: File) : DownloadStatus() //完成
}



