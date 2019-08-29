package com.example.a100gram.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a100gram.R
import com.example.a100gram.models.Article
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleRecyclerViewAdapter :
    RecyclerView.Adapter<ArticleRecyclerViewAdapter.ArticleViewHolder>() {

    private lateinit var mOnArticleClickListener: OnArticleClickListener

    private val mListOfArticles = mutableListOf<Article>()

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bindViews(mListOfArticles[position])
    }

    override fun getItemCount(): Int = mListOfArticles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(v)
    }

    fun setOnArticleClickListener(onArticleClickListener: OnArticleClickListener) {
        mOnArticleClickListener = onArticleClickListener
    }

    fun addArticle(article: Article) = mListOfArticles.add(article)

    fun reverseList() = mListOfArticles.reverse()

    inner class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bindViews(article: Article) {
            val title = view.tv_item_article_title
            val content = view.tv_item_article_content
            val date = view.tv_item_article_date

            title.text = article.title
            content.text = article.content
            date.text = article.date

            // open an article
            view.cv_item_article.setOnClickListener {
                mOnArticleClickListener.onClick(article)
            }
        }

    }

    interface OnArticleClickListener {
        fun onClick(article: Article)
    }

}