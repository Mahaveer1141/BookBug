package com.example.bookapp

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CollectionAdapter(private val list : ArrayList<Book>, private val context: Context) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionAdapter.ViewHolder {
        return CollectionAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.collection_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = list[position]
        holder.title.text = book.title
        Picasso.get().load(book.photoURL).into(holder.image)
        holder.linearLayout.setOnClickListener {
            val data = "${book.title}\n${book.id}\n${book.author}\n${book.photoURL}\n${book.description}\n"
            val intent = Intent(context, ShowBookActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title : TextView = itemView.findViewById(R.id.book_title)
        var image : ImageView = itemView.findViewById(R.id.book_image)
        var linearLayout : LinearLayout = itemView.findViewById(R.id.linearLayout)
    }
}