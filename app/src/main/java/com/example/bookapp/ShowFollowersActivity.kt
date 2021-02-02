package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ShowFollowersActivity : AppCompatActivity() {
    lateinit var show_followers : TextView
    lateinit var recyclerView: RecyclerView
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_followers)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Followers");
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val user_id = intent.getStringExtra("id")!!

        show_followers = findViewById(R.id.show_followers)
        recyclerView = findViewById(R.id.user_re)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        db.collection("following").get()
                .addOnSuccessListener { result->
                    val list : ArrayList<String> = ArrayList()
                    for (document in result) {
                        val data = document.data["follow_list"] as ArrayList<*>
                        for (i in data) {
                            if (i.toString() == user_id) list.add(document.id)
                        }
                    }
                    show_followers.text = "User have ${list.size} Followers"
                    val adapter : FollowAdapter = FollowAdapter(list, this)
                    recyclerView.adapter = adapter
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}