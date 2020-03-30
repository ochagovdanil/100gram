package com.example.a100gram.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a100gram.R
import com.example.a100gram.activities.ChangePasswordActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(context).inflate(R.layout.fragment_profile, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initApp()
    }

    private fun initApp() {
        mAuth = FirebaseAuth.getInstance()

        setEmailAddress()
        changePassword()
        signOut()
    }

    private fun setEmailAddress() {
        val user = mAuth.currentUser

        // set a user email address
        tv_profile_fragment_email.text = user?.email

        // set if a user email address is verified
        var str = "Error, you're not verified! How'd you get here?"

        if (user!!.isEmailVerified) {
            str = "verified"
        }

        tv_profile_fragment_email_verified.text = str
    }

    private fun changePassword() {
        tv_profile_fragment_change_password.setOnClickListener {
            // show Ask Dialog Fragment
            val bundle = Bundle().apply {
                putString("message", tv_profile_fragment_change_password.text.toString())
            }

            AskDialogFragment().apply {
                arguments = bundle
                setPositiveButtonClickListener(object : AskDialogFragment.PositiveButtonClickListener {
                    override fun setOnPositiveButtonClickListener() {
                        // go to Change Password Activity
                        startActivity(Intent(context, ChangePasswordActivity::class.java))
                    }
                })
            }.show(activity?.supportFragmentManager!!, "AskDialogFragment")
        }
    }

    private fun signOut() {
        tv_profile_fragment_sign_out.setOnClickListener {
            // show Ask Dialog Fragment
            val bundle = Bundle().apply {
                putString("message", tv_profile_fragment_sign_out.text.toString())
            }

            AskDialogFragment().apply {
                arguments = bundle
                setPositiveButtonClickListener(object : AskDialogFragment.PositiveButtonClickListener {
                    override fun setOnPositiveButtonClickListener() {
                        // sign out and go to Register Activity
                        mAuth.signOut()
                        activity?.recreate()
                    }
                })
            }.show(activity?.supportFragmentManager!!, "AskDialogFragment")
        }
    }

}