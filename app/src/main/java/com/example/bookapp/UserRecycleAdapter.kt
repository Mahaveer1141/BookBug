package com.example.bookapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class UserRecycleAdapter(private val arrayList : ArrayList<User>, private val context: Context) : RecyclerView.Adapter<UserRecycleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRecycleAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_search_layout, parent, false)
        return UserRecycleAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: UserRecycleAdapter.ViewHolder, position: Int) {
        val user : User = arrayList[position]
        holder.user_name.text = user.name
        Picasso.get().load(user.photoURL).into(holder.user_image)
        holder.linearLayout.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id", user.id)
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name : TextView = itemView.findViewById(R.id.user_name)
        var user_image : ImageView = itemView.findViewById(R.id.user_image)
        var linearLayout : LinearLayout = itemView.findViewById(R.id.main_layout)
    }
}
