package com.example.a100gram.fragments

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.a100gram.R
import com.example.a100gram.helpers.ProgressBarDialog
import com.example.a100gram.models.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_new_article.*
import kotlinx.android.synthetic.main.partial_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class NewArticleFragment : Fragment() {

    private lateinit var mEditTextTitle: EditText
    private lateinit var mEditTextContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // init the toolbar menu options
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(context).inflate(R.layout.fragment_new_article, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initApp()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.new_article, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.item_new_article_fragment_publish -> {
            checkForm()
            true
        }
        else -> false
    }

    private fun initApp() {
        initToolbar()

        mEditTextTitle = et_new_article_fragment_title
        mEditTextContent = et_new_article_fragment_content
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar_custom.apply {
            title = getString(R.string.toolbar_new_article_fragment_title)
        })
    }

    private fun checkForm() {
        // clear old errors
        mEditTextTitle.error = null
        mEditTextContent.error = null

        // get the fields
        val title = mEditTextTitle.text.toString()
        val content = mEditTextContent.text.toString()

        // check the form
        if (title.isEmpty()) {
            mEditTextTitle.error = "You must fill this field out!"
            return
        }

        if (title.length < 10) {
            mEditTextTitle.error = "The title is short!"
            return
        }

        if (content.isEmpty()) {
            mEditTextContent.error = "You must fill this field out!"
            return
        }

        if (content.length < 20) {
            mEditTextContent.error = "The content is short!"
            return
        }

        // everything's okay, let's post an article
        val user = FirebaseAuth.getInstance().currentUser

        postArticle(
            Article(
                user?.uid,
                user?.email,
                title,
                content,
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
            )
        )
    }

    private fun postArticle(article: Article) {
        ProgressBarDialog.showDialog(activity as AppCompatActivity)

        FirebaseDatabase.getInstance().reference.child("articles").push().setValue(article)
            .addOnCompleteListener { task ->
                ProgressBarDialog.hideDialog(activity as AppCompatActivity)

                if (task.isSuccessful) {
                    // everything's okay
                    mEditTextTitle.text.clear()
                    mEditTextContent.text.clear()
                    mEditTextTitle.error = null
                    mEditTextContent.error = null

                    showMessage("Your article was successfully posted!")
                } else {
                    showMessage("Something went wrong[setValue]")
                }
            }
    }

    private fun showMessage(message: String) {
        InformationDialogFragment().apply {
            arguments = Bundle().apply {
                putString("message", message)
            }
        }.show(activity?.supportFragmentManager, "InformationDialogFragment")
    }

}