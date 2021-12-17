package com.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.simple.MViewModel
import com.example.simple.R
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //fragment的容器视图，navHost的默认实现——NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        //管理应用导航的对象
        val navController = navHostFragment.navController

        //fragment与BottomNavigationView的交互交给NavigationUI
        bottom_nav_view.setupWithNavController(navController)

    }

}
