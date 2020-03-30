package com.example.a100gram.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InformationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(context).apply {
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