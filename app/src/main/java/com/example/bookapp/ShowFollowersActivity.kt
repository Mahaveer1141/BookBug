package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ShowFollowersActivity : AppCompatActivity() {
    lateinit var show_followers : TextView
    lateinit var recyclerView: RecyclerView
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_followers)

        val user_id = intent.getStringExtra("id")!!

        show_followers = findViewById(R.id.show_followers)
        recyclerView = findViewById(R.id.user_re)



    }
}