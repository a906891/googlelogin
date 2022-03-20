package com.example.googlelogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signed_in.*

class SignedIn : AppCompatActivity(),NewsItemClicked {

    private lateinit var mAuth:FirebaseAuth

    //News
    private lateinit var mAdapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)

        // Managing the layout of the View
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchdata()
        mAdapter  = NewsListAdapter(this)
        recyclerView.adapter = mAdapter


        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        nameText.text = "Welcome,"+currentUser?.displayName
        emailText.text = "Email "+currentUser?.email

        Glide.with(this).load(currentUser?.photoUrl).into(imageView)

        signout.setOnClickListener{
            mAuth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun fetchdata() {
        val url = "https://newsapi.org/v2/top-headlines?country=in&apiKey=b26ebc337a4d4e2e9b2a8e442a5fb334"
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                val newsJsonArray = it.getJSONArray("articles")
                Log.d("niku", newsJsonArray.toString())
                val newsArray = ArrayList<News>()
                for(i in 0 until  newsJsonArray.length()){
                    val newsJsonObject = newsJsonArray.getJSONObject(i)  // getting the articles array from the json object
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }

                mAdapter.updateNews(newsArray)

            },
            {

            }

        )
        // Dont know what this is but this makes the whole thing work **
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        Log.d("niku","called singleton")
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    override fun onItemClicked(item: News) {

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}