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
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPassword: EditText
    private lateinit var mEditTextPasswordRepeat: EditText
    private lateinit var mTextInputEmail: TextInputLayout
    private lateinit var mTextInputPassword: TextInputLayout
    private lateinit var mTextInputPasswordRepeat: TextInputLayout

    private lateinit var mAuth: FirebaseAuth

    private val mHandler = Handler()

    private var mBackPressedTime: Long = -1 // it's used to realize "Press back again to exit" feature
    private var mInternetConnectionState = false // if the Internet connection is well the variable will be True

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
        mEditTextEmail = et_register_activity_email
        mEditTextPassword = et_register_activity_password
        mEditTextPasswordRepeat = et_register_activity_password_repeat
        mTextInputEmail = til_register_activity_email
        mTextInputPassword = til_register_activity_password
        mTextInputPasswordRepeat = til_register_activity_password_repeat
        mAuth = FirebaseAuth.getInstance()

        checkForm()
        alreadyHaveAccount()
    }

    private fun checkForm() {
        btn_register_activity_submit.setOnClickListener {
            // clear old errors
            mTextInputEmail.error = ""
            mTextInputPassword.error = ""
            mTextInputPasswordRepeat.error = ""

            // get the fields
            val email = mEditTextEmail.text.toString()
            val password = mEditTextPassword.text.toString()
            val passwordRepeat = mEditTextPasswordRepeat.text.toString()

            // check the form
            if (email.isEmpty()) {
                mTextInputEmail.error = "You must fill this field out!"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                mTextInputPassword.error = "You must fill this field out!"
                return@setOnClickListener
            }

            if (passwordRepeat.isEmpty()) {
                mTextInputPasswordRepeat.error = "You must fill this field out!"
                return@setOnClickListener
            }

            if (password != passwordRepeat) {
                showMessage("The passwords don't match!")
                return@setOnClickListener
            }

            // everything's okay, let's sign up
            signUp(email, passwordRepeat)
        }
    }

    private fun signUp(email: String, password: String) {
        ProgressBarDialog.showDialog(this)

        // create an user account
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // send the email verification
                mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                    ProgressBarDialog.hideDialog(this)

                    if (task.isSuccessful) {
                        // everything's okay
                        mAuth.signOut()

                        mEditTextEmail.text.clear()
                        mEditTextPassword.text.clear()
                        mEditTextPasswordRepeat.text.clear()
                        mTextInputEmail.error = ""
                        mTextInputPassword.error = ""
                        mTextInputPasswordRepeat.error = ""

                        showMessage("Check your email box before signing in!")
                    } else {
                        showMessage("Something went wrong[sendEmailVerification]")
                    }
                }
            } else {
                ProgressBarDialog.hideDialog(this)

                when ((task.exception as FirebaseAuthException).errorCode) {
                    "ERROR_INVALID_EMAIL" -> {
                        mTextInputEmail.error = "The email address isn't valid!"
                    }
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        showMessage("Account with the same email address already exists!")
                    }
                    "ERROR_WEAK_PASSWORD" -> {
                        showMessage("The password is weak!")
                    }
                    else -> showMessage("Something went wrong[createUserWithEmailAndPassword]")
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

    private fun alreadyHaveAccount() {
        tv_register_activity_have_account.setOnClickListener {
            // go to Login Activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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

                    InternetConnectionManager.setGoodConnection(this@RegisterActivity)
                }
            } else {
                if (mInternetConnectionState) {
                    mInternetConnectionState = false

                    InternetConnectionManager.setPoorConnection(
                        this@RegisterActivity,
                        this@RegisterActivity
                    )
                }
            }

            mHandler.postDelayed(this, 500)
        }
    }

}
