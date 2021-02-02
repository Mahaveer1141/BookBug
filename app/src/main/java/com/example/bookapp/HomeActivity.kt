package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: PostListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Home");

        mAuth = FirebaseAuth.getInstance()

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        recyclerView = findViewById(R.id.list_view)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val query = db.collection("posts").orderBy("time", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<PostInformation>().setQuery(query, PostInformation::class.java).build()
        adapter = PostListAdapter(recyclerViewOptions, this, this@HomeActivity)
        recyclerView.adapter = adapter


        val cardView : CardView = findViewById(R.id.create_post)
        cardView.setOnClickListener {
            val intent: Intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }

        val profile : ImageView = findViewById(R.id.profile)
        Picasso.get().load(mAuth.currentUser?.photoUrl.toString()).into(profile);
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.profile_page -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }

            R.id.add -> {
                val intent = Intent(this, AddBookActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.search -> {
                val intent = Intent(this, SearchUserActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun getPostbyId(postId: String) : Task<DocumentSnapshot> {
        return db.collection("posts").document(postId).get()
    }

    fun onLikeCLicked(postId : String) {
        GlobalScope.launch {
            mAuth = FirebaseAuth.getInstance()
            val post = getPostbyId(postId).await().toObject(PostInformation::class.java)!!
            val isLiked = post.likedBy.contains(mAuth.currentUser?.uid)
            if (isLiked) {
                post.likedBy.remove(mAuth.currentUser!!.uid)
            } else {
                post.likedBy.add(mAuth.currentUser!!.uid)
            }
            db.collection("posts").document(postId).set(post)
        }
    }

}
