package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ShowCollectonActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_collecton)

        val user_id = intent.getStringExtra("user_id")!!
        val collection_size : TextView = findViewById(R.id.size)
        recyclerView = findViewById(R.id.collection_recycle)

        recyclerView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)

        db.collection("users").document(user_id)
            .get().addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject(User::class.java)!!
                    val adapter : CollectionAdapter = CollectionAdapter(user.bookList, this)
                    collection_size.text = "User's Collecton (${user.bookList.size} Books)"
                    recyclerView.adapter = adapter
                }
            }

    }
}
