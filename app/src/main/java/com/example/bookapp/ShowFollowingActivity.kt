package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ShowFollowingActivity : AppCompatActivity() {
    lateinit var show_followers : TextView
    lateinit var recyclerView: RecyclerView
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_following)

        val user_id = intent.getStringExtra("id")!!

        show_followers = findViewById(R.id.show_followers)
        recyclerView = findViewById(R.id.user_re)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        db.collection("following").document(user_id)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val follow_list = it.toObject(Follow::class.java)!!.follow_list
                    show_followers.text = " User follow ${follow_list.size.toString()} user"
                    val adapter : FollowAdapter = FollowAdapter(follow_list, this)
                    recyclerView.adapter = adapter
                }
            }

    }
}