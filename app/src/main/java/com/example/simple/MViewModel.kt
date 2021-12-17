package com.example.simple

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MViewModel() : ViewModel() {

    val TAG = "mViewModel"
    fun simpleFlow2() = flow<Int> {
        println("Flow started")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }


    }

    fun testCold(){

        viewModelScope.launch {
            val flow = simpleFlow2()
            println("Calling collect...")
            flow.collect { value -> println(value) }

            println("Calling collect again...")
            flow.collect { value -> println(value) }
        }
    }
    val _events = MutableSharedFlow<String>()

    //这里会每隔1s发送一个数据
    suspend fun foo1(){
        Log.i(TAG, "foo: 开始发送数据")
        Log.i(TAG, "foo: 开始发送A")
        _events.emit("A")
        Log.i(TAG, "foo: 结束发送A")
        delay(1000)
        Log.i(TAG, "foo: 开始发送B")
        _events.emit("B")
        Log.i(TAG, "foo: 结束发送B")
        delay(1000)
        Log.i(TAG, "foo: 开始发送C")
        _events.emit("C")
        Log.i(TAG, "foo: 结束发送C")
        Log.i(TAG, "foo: 结束发送数据")
    }

    fun testHot(){
        //先开启协程，创建出SharedFlow
        viewModelScope.launch {
            foo1()
        }

        //立马进行收集
        viewModelScope.launch(Dispatchers.IO) {
            _events.collect {
                Log.i(TAG, "initData: A开始收集 $it")
            }
        }
        //延迟2秒再进行收集
        viewModelScope.launch {
            delay(2000)
            _events.collect {
                Log.i(TAG, "initData: B开始收集 $it")
            }
        }
    }
}