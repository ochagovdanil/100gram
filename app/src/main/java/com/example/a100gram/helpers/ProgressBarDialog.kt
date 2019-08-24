package com.example.a100gram.helpers

import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.a100gram.R

object ProgressBarDialog {

    fun showDialog(activity: AppCompatActivity) {
        // show the progress bar
        activity.findViewById<RelativeLayout>(R.id.pb_layout).visibility = View.VISIBLE

        // disable to interact with the display
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun hideDialog(activity: AppCompatActivity) {
        // hide the progress bar
        activity.findViewById<RelativeLayout>(R.id.pb_layout).visibility = View.GONE

        // enable to interact with the display
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}