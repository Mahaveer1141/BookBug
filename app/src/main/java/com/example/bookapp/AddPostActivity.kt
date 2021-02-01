package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddPostActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val profile : ImageView = findViewById(R.id.profile)
        Picasso.get().load(currentUser?.photoUrl.toString()).into(profile);

        val backButton : ImageView = findViewById(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val editText : EditText = findViewById(R.id.post_text)
        val button : Button = findViewById(R.id.add_post)
        button.setOnClickListener {
            val text : String = editText.text.toString().trim()
            if (text != "") {
                GlobalScope.launch {
                    val currentUserId = mAuth.currentUser!!.uid
                    val user = getUserById(currentUserId).await().toObject(User::class.java)!!
                    val currentTime = System.currentTimeMillis()
                    val post = PostInformation(text, user, currentTime)
                    postsCollection.document().set(post)
                }
                Toast.makeText(this, "Post is added", Toast.LENGTH_SHORT).show();
                finish()
            }
            else  {
                Toast.makeText(this, "Empty post can not be posted", Toast.LENGTH_SHORT).show();
            }

        }
    }

    fun getUserById(uId: String): Task<DocumentSnapshot> {
        return db.collection("users").document(uId).get()
    }

}