package com.example.bookapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class PostListAdapter(options: FirestoreRecyclerOptions<PostInformation>, val listner: HomeActivity, val context: Context) : FirestoreRecyclerAdapter<PostInformation, PostListAdapter.PostViewHolder>(options) {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false))
        viewHolder.like_button.setOnClickListener {
            listner.onLikeCLicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: PostInformation) {
        mAuth = FirebaseAuth.getInstance()
        holder.postTextView.text = model.text
        holder.name.text = model.user.name
        Picasso.get().load(model.user.photoURL).into(holder.profile)
        holder.created_at.text = Utils.getTimeAgo(model.time)
        holder.like_count.text = model.likedBy.size.toString()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUser)
        if (isLiked) {
            holder.like_button.setImageDrawable(ContextCompat.getDrawable(holder.like_button.context, R.drawable.ic_liked))
        } else {
            holder.like_button.setImageDrawable(ContextCompat.getDrawable(holder.like_button.context, R.drawable.ic_baseline_favorite_border_24))
        }
        holder.linearLayout.setOnClickListener {
            if (mAuth.currentUser!!.uid == model.user.id) {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ShowUserActivity::class.java)
                intent.putExtra("id", model.user.id)
                context.startActivity(intent)
            }
        }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postTextView : TextView = itemView.findViewById(R.id.post_text)
        var name : TextView = itemView.findViewById(R.id.name)
        var profile : ImageView = itemView.findViewById(R.id.profile_photo)
        var like_button : ImageView = itemView.findViewById(R.id.like_button)
        var created_at : TextView = itemView.findViewById(R.id.created_at)
        var like_count : TextView = itemView.findViewById(R.id.like_count)
        var linearLayout : LinearLayout = itemView.findViewById(R.id.linear)
    }

}

interface IPostListAdapter {
    fun onLikeCLicked(postId : String)
}

