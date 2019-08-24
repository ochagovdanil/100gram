package com.example.a100gram.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.a100gram.R

class AskDialogFragment : DialogFragment() {

    private lateinit var mPositiveButtonClickListener: PositiveButtonClickListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(context).apply {
            setCancelable(false)
            setTitle("Warning!")
            setIcon(R.drawable.ic_dialog_warning)
            setMessage(
                arguments?.getString(
                    "message",
                    "Couldn't get a message for AskDialog Fragment!"
                )
            )
            setPositiveButton("Accept") { _, _ ->
                mPositiveButtonClickListener.setOnPositiveButtonClickListener()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

    fun setPositiveButtonClickListener(positiveButtonClickListener: PositiveButtonClickListener) {
        mPositiveButtonClickListener = positiveButtonClickListener
    }

    interface PositiveButtonClickListener {
        fun setOnPositiveButtonClickListener()
    }

}