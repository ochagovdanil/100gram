package com.example.a100gram.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a100gram.R
import kotlinx.android.synthetic.main.activity_view_article.*

class ViewArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_article)

        initArticle()
    }

    private fun initArticle() {
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val email = intent.getStringExtra("email")
        val date = intent.getStringExtra("date")

        tv_view_article_activity_title.text = title
        tv_view_article_activity_content.text = content
        tv_view_article_activity_email.text = email
        tv_view_article_activity_date.text = date
    }

}