package com.example.a100gram.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a100gram.R
import com.example.a100gram.fragments.NewArticleFragment
import com.example.a100gram.fragments.ArticlesFragment
import com.example.a100gram.fragments.ProfileFragment
import com.example.a100gram.helpers.InternetConnectionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private var mBackPressedTime: Long = -1 // it's used to realize "Press back again to exit" feature
    private var mInternetConnectionState = false // if the Internet connection is well the variable will be True

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check for a user session
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        } else {
            initApp()
        }
    }

    override fun onStart() {
        super.onStart()

        // check for a user session
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // stop the checking for the Internet connection
        mHandler.removeCallbacks(mInternetConnection)
    }

    override fun onBackPressed() {
        if (mBackPressedTime + 2_000 > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }

        mBackPressedTime = System.currentTimeMillis()
    }

    private fun initApp() {
        // set up the Internet connection
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        mInternetConnectionState = !(activeNetworkInfo != null && activeNetworkInfo.isConnected)
        mHandler.post(mInternetConnection)

        // init the app
        // set the default fragment - Articles Fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_main_activity_container, ArticlesFragment()).commit()

        // set up the listener
        bnv_main_activity.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_main_activity_bottom_navigation_articles -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_main_activity_container, ArticlesFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.item_main_activity_bottom_navigation_new_article -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_main_activity_container, NewArticleFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.item_main_activity_bottom_navigation_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_main_activity_container, ProfileFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private var mInternetConnection: Runnable = object : Runnable {
        override fun run() {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo

            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                if (!mInternetConnectionState) {
                    mInternetConnectionState = true

                    InternetConnectionManager.setGoodConnection(this@MainActivity)
                }
            } else {
                if (mInternetConnectionState) {
                    mInternetConnectionState = false

                    InternetConnectionManager.setPoorConnection(
                        this@MainActivity,
                        this@MainActivity
                    )
                }
            }

            mHandler.postDelayed(this, 500)
        }
    }

}
