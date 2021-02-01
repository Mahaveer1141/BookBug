package com.example.bookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BookSearchAdapter(private val arrayList : ArrayList<Book>) : RecyclerView.Adapter<BookSearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.book_search_layout, parent, false)
        return BookSearchAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: BookSearchAdapter.ViewHolder, position: Int) {
        val book : Book = arrayList[position]
        holder.book_title.text = book.title
        holder.book_author.text = book.author
        if (book.photoURL != "") Picasso.get().load(book.photoURL).into(holder.book_photo)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var book_author : TextView = itemView.findViewById(R.id.book_author)
        var book_title : TextView = itemView.findViewById(R.id.book_title)
        var book_photo : ImageView = itemView.findViewById(R.id.book_image)
    }

}
