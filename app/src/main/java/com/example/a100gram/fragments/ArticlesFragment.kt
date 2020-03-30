package com.example.a100gram.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a100gram.R
import com.example.a100gram.adapters.ArticleRecyclerViewAdapter
import com.example.a100gram.helpers.ProgressBarDialog
import com.example.a100gram.models.Article
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_articles.*
import kotlinx.android.synthetic.main.partial_toolbar.*

class ArticlesFragment : Fragment() {

    private lateinit var mAdapter: ArticleRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // init the toolbar menu options
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutInflater.from(context).inflate(R.layout.fragment_articles, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initApp()
    }

    private fun initApp() {
        mAdapter = ArticleRecyclerViewAdapter(context!!)

        initToolbar()
        initList()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar_custom.apply {
            title = getString(R.string.toolbar_articles_fragment_title)
        })
    }

    private fun initList() {
        ProgressBarDialog.showDialog(activity as AppCompatActivity)

        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child("articles").exists()) {
                    // everything's okay, let's retrieve the data
                    for (snapshot in p0.child("articles").children) {
                        val article: Article = snapshot.getValue(Article::class.java)!!
                        mAdapter.addArticle(article)
                    }

                    // show the newest articles
                    mAdapter.reverseList()

                    initRecyclerViewList()
                } else {
                    ProgressBarDialog.hideDialog(activity as AppCompatActivity)

                    Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                ProgressBarDialog.hideDialog(activity as AppCompatActivity)

                showMessage("Something went wrong[addListenerForSingleValueEvent]")
            }
        })
    }

    private fun initRecyclerViewList() {
        val recyclerView = rv_articles_fragment

        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.scrollToPosition(0) // scroll to the top
        recyclerView?.adapter = mAdapter

        ProgressBarDialog.hideDialog(activity as AppCompatActivity)
    }

    private fun showMessage(message: String) {
        InformationDialogFragment().apply {
            arguments = Bundle().apply {
                putString("message", message)
            }
        }.show(activity?.supportFragmentManager!!, "InformationDialogFragment")
    }

}