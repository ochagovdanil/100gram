package com.example.a100gram.activities

import android.content.Context
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPassword: EditText
    private lateinit var mTextInputEmail: TextInputLayout
    private lateinit var mTextInputPassword: TextInputLayout

    private lateinit var mAuth: FirebaseAuth

    private val mHandler = Handler()

    private var mBackPressedTime: Long = -1 // it's used to realize "Press back again to exit" feature
    private var mInternetConnectionState = false // if the Internet connection is well the variable will be True

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initApp()
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
        mEditTextEmail = et_login_activity_email
        mEditTextPassword = et_login_activity_password
        mTextInputEmail = til_login_activity_email
        mTextInputPassword = til_login_activity_password
        mAuth = FirebaseAuth.getInstance()

        checkForm()
        dontHaveAccount()
        resetPassword()
    }

    private fun checkForm() {
        btn_login_activity_submit.setOnClickListener {
            // clear old errors
            mTextInputEmail.error = ""
            mTextInputPassword.error = ""

            // get the fields
            val email = mEditTextEmail.text.toString()
            val password = mEditTextPassword.text.toString()

            // check the form
            if (email.isEmpty()) {
                mTextInputEmail.error = "You must fill this field out!"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                mTextInputPassword.error = "You must fill this field out!"
                return@setOnClickListener
            }

            // everything's okay, let's sign in
            signIn(email, password)
        }
    }

    private fun signIn(email: String, password: String) {
        ProgressBarDialog.showDialog(this)

        // sign in a user account
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (mAuth.currentUser!!.isEmailVerified) {
                    // everything's okay, let's start Main Activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    ProgressBarDialog.hideDialog(this)

                    showMessage("You must verify your email address before signing in!")
                }
            } else {
                ProgressBarDialog.hideDialog(this)

                when ((task.exception as FirebaseAuthException).errorCode) {
                    "ERROR_INVALID_EMAIL" -> {
                        mTextInputEmail.error = "The email address isn't valid!"
                    }
                    "ERROR_WRONG_PASSWORD" -> {
                        showMessage("The password is wrong!")
                    }
                    "ERROR_USER_DISABLED" -> {
                        showMessage("Your account has been disabled!")
                    }
                    "ERROR_USER_NOT_FOUND" -> {
                        showMessage("The user wasn't found!")
                    }
                    else -> showMessage("Something went wrong[signInWithEmailAndPassword]")
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

    private fun dontHaveAccount() {
        tv_login_activity_dont_have_account.setOnClickListener {
            // go to Register Activity
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun resetPassword() {
        tv_login_activity_reset_password.setOnClickListener {
            // clear old errors
            mTextInputEmail.error = ""

            // check the form
            val email = mEditTextEmail.text.toString()

            if (email.isEmpty()) {
                mTextInputEmail.error = "You must fill this field out!"
                return@setOnClickListener
            }

            ProgressBarDialog.showDialog(this)

            // reset a user password
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                ProgressBarDialog.hideDialog(this)

                if (task.isSuccessful) {
                    // everything's okay
                    showMessage("Check your email box!")
                } else {
                    when ((task.exception as FirebaseAuthException).errorCode) {
                        "ERROR_INVALID_EMAIL" -> {
                            mTextInputEmail.error = "The email address isn't valid!"
                        }
                        "ERROR_USER_NOT_FOUND" -> {
                            showMessage("The user wasn't found!")
                        }
                        else -> showMessage("Something went wrong[sendPasswordResetEmail]")
                    }
                }
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

                    InternetConnectionManager.setGoodConnection(this@LoginActivity)
                }
            } else {
                if (mInternetConnectionState) {
                    mInternetConnectionState = false

                    InternetConnectionManager.setPoorConnection(
                        this@LoginActivity,
                        this@LoginActivity
                    )
                }
            }

            mHandler.postDelayed(this, 500)
        }
    }

}