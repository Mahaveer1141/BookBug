package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShowPostActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: PostListAdapter
    private lateinit var post_count: TextView
    var user_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Posts");
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);


        user_id = intent.getStringExtra("id")!!


        post_count = findViewById(R.id.post_count)

        recyclerView = findViewById(R.id.list_view)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        show_posts()

        val query = db.collection("posts").whereEqualTo("user.id", user_id).orderBy("time", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<PostInformation>().setQuery(query, PostInformation::class.java).build()
        adapter = PostListAdapter(recyclerViewOptions, HomeActivity(), this@ShowPostActivity)
        recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    fun show_posts() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                var count = 0
                for (document in result) {
                    val i = document.data["user"] as Map<*, *>
                    if (i["id"] == user_id) count++
                }
                post_count.text = "User have $count Post"
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}