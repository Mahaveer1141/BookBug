package com.example.bookapp

import android.content.Intent
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
import kotlin.math.min

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

        show_followers.setOnClickListener {
            val intent = Intent(this, ShowFollowersActivity::class.java)
            intent.putExtra("id", user_id)
            startActivity(intent)
        }

        show_following.setOnClickListener {
            val intent = Intent(this, ShowFollowingActivity::class.java)
            intent.putExtra("id", user_id)
            startActivity(intent)
        }


        show_posts()
        show_following()
        show_followers()
        show_books()

        val image: ImageView = findViewById(R.id.user_image)
        val name: TextView = findViewById(R.id.name)
        val more: TextView = findViewById(R.id.more)
        val follow_button: Button = findViewById(R.id.follow)

        more.setOnClickListener {
            val intent = Intent(this, ShowCollectonActivity::class.java)
            intent.putExtra("user_id", user_id)
            startActivity(intent)
        }

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

    fun show_books() {
        db.collection("users").document(user_id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.toObject(User::class.java)!!
                        val imageViewArr : ArrayList<Int> = ArrayList()
                        imageViewArr.add(R.id.imageView1)
                        imageViewArr.add(R.id.imageView2)
                        imageViewArr.add(R.id.imageView3)
                        if (!user.bookList.isEmpty()) {
                            for (i in 0..min(2, user.bookList.size-1)) {
                                val imageView : ImageView = findViewById(imageViewArr[i])
                                Picasso.get().load(user.bookList[i].photoURL.toString()).into(imageView)
                            }
                        }
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
