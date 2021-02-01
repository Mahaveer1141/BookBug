package com.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ShowBookActivity : AppCompatActivity() {
    val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var mAuth: FirebaseAuth
    lateinit var title: TextView
    lateinit var photoUrl: ImageView
    lateinit var description: TextView
    lateinit var author: TextView
    lateinit var add: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_book)

        mAuth = FirebaseAuth.getInstance()

        title = findViewById(R.id.title)
        photoUrl = findViewById(R.id.image)
        description = findViewById(R.id.description)
        author = findViewById(R.id.author)
        add = findViewById(R.id.add_book)

        val book_data = intent.getStringExtra("data")!!.split("\n")
        val book : Book = Book(book_data[0], book_data[2], book_data[3], book_data[1], book_data[4])

        db.collection("users").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject(User::class.java)!!
                if (user.bookList.contains(book)) {
                    add.text = "Remove"
                } else {
                    add.text = "Add"
                }
            }
        }

        title.text = book_data[0]
        description.text = book_data[4]
        author.text = book_data[2]
        Picasso.get().load(book_data[3]).into(photoUrl)


        add.setOnClickListener {
            db.collection("users").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject(User::class.java)!!
                    if (user.bookList.contains(book)) {
                        user.bookList.remove(book)
                        add.text = "Add"
                    } else {
                        user.bookList.add(book)
                        add.text = "Remove"
                    }
                    db.collection("users").document(mAuth.currentUser!!.uid).set(user)
                }
            }

        }
    }
}
