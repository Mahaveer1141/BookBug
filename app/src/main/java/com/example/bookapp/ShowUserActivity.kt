package com.example.bookapp

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShowUserActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var mAuth: FirebaseAuth
    lateinit var show_post: TextView
    lateinit var show_following: TextView
    lateinit var show_followers: TextView
    var user_id = ""
    val userCollection = db.collection("users")
    val followingCollecion = db.collection("following")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        user_id = intent.getStringExtra("id")!!

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser!!
        show_post = findViewById(R.id.show_post)
        show_following = findViewById(R.id.show_followings)
        show_followers = findViewById(R.id.show_followers)

        show_posts()
        show_following()
        show_followers()

        val image: ImageView = findViewById(R.id.user_image)
        val name: TextView = findViewById(R.id.name)
        val follow_button: Button = findViewById(R.id.follow)

        followingCollecion.document(currentUser.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val data = it.toObject(Follow::class.java)!!
                if (data.follow_list.contains(user_id)) {
                    follow_button.text = "Unfollow"
                } else {
                    follow_button.text = "Follow"
                }
            }
        }

        userCollection.document(user_id).get().addOnSuccessListener {
            name.text = it["name"].toString()
            Picasso.get().load(it["photoURL"].toString()).into(image)
        }

        follow_button.setOnClickListener {
            followingCollecion.document(currentUser.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val data = it.toObject(Follow::class.java)!!
                    if (data.follow_list.contains(user_id)) {
                        data.follow_list.remove(user_id)
                        follow_button.text = "Follow"
                    } else {
                        data.follow_list.add(user_id)
                        follow_button.text = "Unfollow"
                    }
                    followingCollecion.document(currentUser.uid).set(data)
                }
                show_followers()
            }
        }
    }

    fun show_following() {
        followingCollecion.document(user_id).get().addOnSuccessListener {
            if (it.exists()) {
                val data = it.toObject(Follow::class.java)!!
                show_following.text = "${data.follow_list.size}\nFollowing"
            }
        }
    }

    fun show_followers() {
        followingCollecion.get()
                .addOnSuccessListener { result->
                    var followers_count = 0
                    for (document in result) {
                        val data = document.data["follow_list"] as ArrayList<*>
                        for (i in data) {
                            if (i.toString() == user_id) followers_count++
                        }
                    }
                    show_followers.text = "$followers_count\nFollowers"
                }
    }

    fun show_posts() {
        db.collection("posts")
                .get()
                .addOnSuccessListener { result ->
                    var post_count = 0
                    for (document in result) {
                        val i = document.data["user"] as Map<*, *>
                        if (i["id"] == user_id) post_count++
                    }
                    show_post.text = "$post_count\nPost"
                }
    }

}
