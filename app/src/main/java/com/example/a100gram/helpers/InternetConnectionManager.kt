package com.example.a100gram.helpers

import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object InternetConnectionManager {

    fun setGoodConnection(activity: AppCompatActivity) {
        // enable to interact with the display
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun setPoorConnection(context: Context, activity: AppCompatActivity) {
        // disable to interact with the display
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        Toast.makeText(
            context,
            "The Internet connection is poor. Now you can't interact with the app!",
            Toast.LENGTH_LONG
        ).show()
    }

}