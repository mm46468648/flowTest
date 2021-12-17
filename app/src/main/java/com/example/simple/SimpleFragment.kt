package com.example.simple

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.room.UserViewModel
import com.example.simple.databinding.FragmentSimpleBinding
import com.example.simple.databinding.FragmentUserBinding
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class SimpleFragment : Fragment() {

    private val viewModel by viewModels<MViewModel>()
    val TAG = "simple"
    lateinit var flow: Flow<Int>
    //viewBinding固定代码
    private val mBinding: FragmentSimpleBinding by lazy {
        FragmentSimpleBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifeCycle()
    }

    /**
     * 简单的创建
     */
    fun simpleCreate() {
        GlobalScope.launch {
            val flow1 = flow {
                emit(1)
            }

            flow1.collect {
                print(it)
            }


        }


        val observable = Observable.create<Int> { emitter ->
            emitter.onNext(1)
        }

        observable.subscribe(Consumer {
            print(it)
        });
    }

    /**
     * 其他创建
     */
    fun otherCreate() {
        val flowOf = flowOf(1, 2, 3, 4, 5)

        GlobalScope.launch {
            flowOf.collect {
                print(it)
            }


        }
        val observable: Observable<*> = Observable.just("A", "B", "C")
        observable.subscribe(object : Observer<Any?> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: Any) {
                print(t)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        });


        listOf(1, 2, 3, 4, 5).asFlow()

        val words = listOf<String>("A", "B", "C")
        val observable1: Observable<String> = Observable.fromIterable(words)
        observable1.subscribe(object : Observer<Any?> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: Any) {
                print(t)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        });
    }

    /**
     * 线程切换
     */
    fun changeThread() {
        GlobalScope.launch {
            flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }.map {
                it * it
            }.flowOn(Dispatchers.IO)
                .collect {
                    println("${Thread.currentThread().name}: $it")
                }
        }


        //

        Observable.just("A", "B", "C").subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Any?> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Any) {
                    print(t)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            });
    }

    /**
     * 取消
     */
    fun cancle() {
        lifecycleScope.launch {
            withTimeoutOrNull(2500) {
                flow {
                    for (i in 1..5) {
                        delay(1000)
                        emit(i)
                    }
                }.collect {
                    println(it)
                }
            }
            println("Done")
        }


        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Long?> {
                var d: Disposable? = null
                override fun onSubscribe(d: Disposable) {
                    this.d = d
                }

                override fun onNext(t: Long) {
                    println(t)
                    if (t > 10) {
                        d?.dispose()
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })

    }

    /**
     * 生命周期
     * 或者可以成为状态回调
     */
    fun lifeCycle() {
        lifecycleScope.launch {
            val time = measureTimeMillis {
                flow {
                    for (i in 0..3) {
                        emit(i.toString())
                    }
                }.onStart {
                    Log.d("xys", "Start Flow in ${Thread.currentThread().name}")
                }.onEach {
                    Log.d("xys", "emit value---$it")
                }.onCompletion {
                    Log.d("xys", "Flow Complete")
                }.collect {
                    Log.d("xys", "Result---$it")
                }
            }
            Log.d("xys", "Time---$time")
        }


        Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                emitter.onNext(1)
                emitter.onNext(2)
                emitter.onNext(3)
                emitter.onNext(4)
            }

        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int?> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("xys", "rx:onSubscribe")
                }

                override fun onNext(t: Int) {
                    Log.d("xys", "rx:onNext---${t}")

                }

                override fun onError(e: Throwable) {
                    Log.d("xys", "rx:onError---${e.toString()}")
                }

                override fun onComplete() {
                    Log.d("xys", "rx:onComplete")
                }
            });

    }

    fun retry() {
        lifecycleScope.launch {
            flow {
                for (i in 0..3) {
                    emit(i.toString())
                }
            }.retryWhen { _, retryCount ->
                retryCount <= 3
            }.onStart {
                Log.d("xys", "Start Flow in ${Thread.currentThread().name}")
            }.onEach {
                Log.d("xys", "emit value---$it")
            }.onCompletion {
                Log.d("xys", "Flow Complete")
            }.collect {
                Log.d("xys", "Result---$it")
            }
        }


        Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                emitter.onNext(1)
                emitter.onNext(2)
                emitter.onNext(3)
                emitter.onNext(4)
                emitter.onComplete()
            }

        }).retry(2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Int?> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("xys", "rx:onSubscribe")
                }

                override fun onNext(t: Int) {
                    Log.d("xys", "rx:onNext---${t}")

                }

                override fun onError(e: Throwable) {
                    Log.d("xys", "rx:onError---${e.toString()}")
                }

                override fun onComplete() {
                    Log.d("xys", "rx:onComplete")
                }
            });


    }

    fun catchException() {
        lifecycleScope.launch {
            flow {
                for (i in 0..3) {
                    emit(i.toString())
                }
                throw Exception("Test")
            }.retryWhen { _, retryCount ->
                retryCount <= 3
            }.onStart {
                Log.d("xys", "Start Flow in ${Thread.currentThread().name}")
            }.onEach {
                Log.d("xys", "emit value---$it")
            }.onCompletion {
                Log.d("xys", "Flow Complete")
            }.catch { error ->
                Log.d("xys", "Flow Error $error")
            }.collect {
                Log.d("xys", "Result---$it")
            }
        }


        lifecycleScope.launch {
            flow {
                for (i in 0..3) {
                    emit(i.toString())
                }
                throw Exception("Test")
            }.retryWhen { _, retryCount ->
                retryCount <= 3
            }.onStart {
                Log.d("xys", "Start Flow in ${Thread.currentThread().name}")
            }.onEach {
                Log.d("xys", "emit value---$it")
            }.onCompletion {e->
                if(e!=null){
                    Log.d("xys", "Flow Exception ${e}")
                }else{
                    Log.d("xys", "Flow Complete")
                }
            }.collect {
                Log.d("xys", "Result---$it")
            }
        }


        Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                emitter.onNext(1)
                emitter.onNext(2)
                emitter.onError(Exception("Test"))
                emitter.onNext(3)
                emitter.onNext(4)
            }

        }).retry(2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Int?> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("xys", "rx:onSubscribe")
                }

                override fun onNext(t: Int) {
                    Log.d("xys", "rx:onNext---${t}")

                }

                override fun onError(e: Throwable) {
                    Log.d("xys", "rx:onError---${e.toString()}")
                }

                override fun onComplete() {
                    Log.d("xys", "rx:onComplete")
                }
            });
    }
}