package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class SearchUserActivity : AppCompatActivity() {
    lateinit var user_recycle_view : RecyclerView
    lateinit var entered_user : EditText
    lateinit var mAuth : FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        mAuth = FirebaseAuth.getInstance()

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.search
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        entered_user = findViewById(R.id.user_search)
        user_recycle_view = findViewById(R.id.recycle_user_views)

        entered_user.setOnEditorActionListener(editorListner)
        user_recycle_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }

    private val editorListner = TextView.OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val user_name = entered_user.text.toString().trim()
            if (user_name == "") {
                Toast.makeText(this, "Please Enter a valid user name", Toast.LENGTH_SHORT).show()
                return@OnEditorActionListener false
            }
            db.collection("users").get()
                    .addOnSuccessListener { result ->
                        val userList : ArrayList<User> = ArrayList()
                        var count = 0
                        for (document in result) {
                            if (document.data["name"] == user_name && mAuth.currentUser?.uid != document.data["id"]) {
                                count++
                                userList.add(User(document.data["name"].toString(), document.data["id"].toString(), document.data["photoURL"].toString()))
                            }
                        }
                        val textView : TextView = findViewById(R.id.count)
                        textView.text = "Results ($count users) - "
                        val adapter : UserRecycleAdapter = UserRecycleAdapter(userList, this)
                        user_recycle_view.adapter = adapter
                    }

            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.profile_page -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }

            R.id.home -> {
                val intent = Intent(this, HomeActivity::class.java)
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
        }
        false
    }
}
