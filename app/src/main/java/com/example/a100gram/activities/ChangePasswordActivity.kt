package com.example.a100gram.activities

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a100gram.R
import com.example.a100gram.fragments.InformationDialogFragment
import com.example.a100gram.helpers.InternetConnectionManager
import com.example.a100gram.helpers.ProgressBarDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.partial_toolbar.*

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var mEditTextOldPassword: EditText
    private lateinit var mEditTextNewPassword: EditText
    private lateinit var mTextInputOldPassword: TextInputLayout
    private lateinit var mTextInputNewPassword: TextInputLayout

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    private val mHandler = Handler()

    private var mInternetConnectionState = false // if the Internet connection is well the variable will be True

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initApp()
    }

    override fun onDestroy() {
        super.onDestroy()

        // stop the checking for the Internet connection
        mHandler.removeCallbacks(mInternetConnection)
    }

    private fun initApp() {
        // set up the Internet connection
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        mInternetConnectionState = !(activeNetworkInfo != null && activeNetworkInfo.isConnected)
        mHandler.post(mInternetConnection)

        // init the app
        initToolbar()

        mEditTextOldPassword = et_change_password_activity_old_password
        mEditTextNewPassword = et_change_password_activity_new_password
        mTextInputOldPassword = til_change_password_activity_old_password
        mTextInputNewPassword = til_change_password_activity_new_password
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        checkForm()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar_custom.apply {
            title = getString(R.string.toolbar_change_password_activity_title)
        })
    }

    private fun checkForm() {
        btn_change_password_activity_submit.setOnClickListener {
            // clear old errors
            mTextInputOldPassword.error = ""
            mTextInputNewPassword.error = ""

            // get the fields
            val email = mUser.email.toString()
            val password = mEditTextOldPassword.text.toString()
            val newPassword = mEditTextNewPassword.text.toString()

            // check the form
            if (password.isEmpty()) {
                mTextInputOldPassword.error = "You must fill this field out!"
                return@setOnClickListener
            }

            if (newPassword.isEmpty()) {
                mTextInputNewPassword.error = "You must fill this field out!"
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                mTextInputNewPassword.error = "The new password is weak!"
                return@setOnClickListener
            }

            if (password == newPassword) {
                showMessage("Your passwords must not be the same!")
                return@setOnClickListener
            }

            // everything's okay, let's update a user password
            updatePassword(email, password, newPassword)
        }
    }

    private fun updatePassword(email: String, oldPassword: String, newPassword: String) {
        ProgressBarDialog.showDialog(this)

        // re-auth the current user
        val credential = EmailAuthProvider.getCredential(email, oldPassword)
        mUser.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // update a user password
                mAuth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener {
                    if (task.isSuccessful) {
                        // everything's okay
                        Toast.makeText(
                            this,
                            "The password was successfully updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        ProgressBarDialog.hideDialog(this)

                        showMessage("Something went wrong[updatePassword]")
                    }
                }
            } else {
                ProgressBarDialog.hideDialog(this)

                when ((task.exception as FirebaseAuthException).errorCode) {
                    "ERROR_WRONG_PASSWORD" -> {
                        showMessage("The old password is wrong!")
                    }
                    "ERROR_USER_DISABLED" -> {
                        showMessage("Your account has been disabled!")
                    }
                    else -> showMessage("Something went wrong[reauthenticate]")
                }
            }
        }
    }

    private fun showMessage(message: String) {
        InformationDialogFragment().apply {
            arguments = Bundle().apply {
                putString("message", message)
            }
        }.show(supportFragmentManager, "InformationDialogFragment")
    }

    private var mInternetConnection: Runnable = object : Runnable {
        override fun run() {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo

            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                if (!mInternetConnectionState) {
                    mInternetConnectionState = true

                    InternetConnectionManager.setGoodConnection(this@ChangePasswordActivity)
                }
            } else {
                if (mInternetConnectionState) {
                    mInternetConnectionState = false

                    InternetConnectionManager.setPoorConnection(
                        this@ChangePasswordActivity,
                        this@ChangePasswordActivity
                    )
                }
            }

            mHandler.postDelayed(this, 500)
        }
    }

}