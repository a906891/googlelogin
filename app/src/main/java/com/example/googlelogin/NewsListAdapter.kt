package com.example.googlelogin

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class NewsListAdapter(private val listener: NewsItemClicked): RecyclerView.Adapter<NewsViewHolder>() {

    private val items: ArrayList<News> = ArrayList()

    // Runs when every new View Holder is made
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        // Converting item_news in View
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener{
            listener.onItemClicked(items[viewHolder.adapterPosition])       // passing the current position of adaptor to main activity by interface
        }
        return viewHolder
    }

    // Binds the data with the Views
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        Log.d("niku","    "+ currentItem.title)
        holder.titleView.text = currentItem.title
        holder.author.text = currentItem.author
        Glide.with(holder.imageView.context).load(currentItem.imageUrl).into(holder.imageView)
    }

    //Runs on first time
    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNews(updatedNews:ArrayList<News>){
        items.clear()
        items.addAll(updatedNews)

        // this allows all upper three functions to call again
        notifyDataSetChanged()
    }
}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.title)
    val imageView: ImageView = itemView.findViewById(R.id.image)
    val author: TextView = itemView.findViewById(R.id.author)

}

interface NewsItemClicked{
    fun onItemClicked(item:News)
}
