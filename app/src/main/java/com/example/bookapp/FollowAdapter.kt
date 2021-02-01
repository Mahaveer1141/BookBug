package com.example.bookapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class FollowAdapter(private val arrayList : ArrayList<String>, private val context: Context) : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    val db = FirebaseFirestore.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.follow_layout, parent, false)
        return FollowAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: FollowAdapter.ViewHolder, position: Int) {
        val mAuth = FirebaseAuth.getInstance()
        val id = arrayList[position]
        db.collection("users").document(id).get()
                .addOnSuccessListener {
                    holder.user_name.text = it.data?.get("name").toString()
                    Picasso.get().load(it.data?.get("photoURL").toString()).into(holder.user_image)
                    if (mAuth.currentUser!!.uid != id) {
                        holder.linearLayout.setOnClickListener {
                            val intent = Intent(context, ShowUserActivity::class.java)
                            intent.putExtra("id", id)
                            context.startActivity(intent)
                        }
                    } else {
                        holder.linearLayout.setOnClickListener {
                            val intent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name : TextView = itemView.findViewById(R.id.user_name)
        var user_image : ImageView = itemView.findViewById(R.id.user_image)
        var linearLayout : LinearLayout = itemView.findViewById(R.id.main_layout)
    }

}
