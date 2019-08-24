package com.example.a100gram.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class InformationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(context).apply {
            setCancelable(true)
            setMessage(
                arguments?.getString(
                    "message",
                    "Couldn't get a message for Information Dialog Fragment!"
                )
            )
            setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

}