package com.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception


class AddBookActivity : AppCompatActivity() {
    val TAG = "MyTag"
    var queue : RequestQueue? = null
    lateinit var entered_book_name : EditText
    lateinit var book_recycle_view : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.add
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        queue = Volley.newRequestQueue(this)

        entered_book_name = findViewById(R.id.book_search)
        book_recycle_view = findViewById(R.id.recycle_book_views)

        entered_book_name.setOnEditorActionListener(editorListner)
        book_recycle_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private val editorListner = TextView.OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val book_name = entered_book_name.text.toString().trim()
            if (book_name == "") {
                Toast.makeText(this, "Please enter a valid book name", Toast.LENGTH_SHORT).show()
                return@OnEditorActionListener false
            }
            var query : String = ""

            val arrayBook = book_name.split(" ")
            for (i in arrayBook) {
                query += i
                query += "+"
            }

            val url = "https://www.googleapis.com/books/v1/volumes?q=$query"
            print("$url \n\n")

            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->
                        val arrayList = response.getJSONArray("items")
                        println("size ${arrayList.length()}\n\n")
                        val bookList : ArrayList<Book> = ArrayList()
                        var count = 0
                        for (i in 0 until arrayList.length()-1) {
                            var description = ""
                            var title = ""
                            var id = ""
                            var author = ""
                            var photoURL = ""
                            count++
                            try {
                                id = response.getJSONArray("items").getJSONObject(i).getString("id").toString()
                            } catch (e : Exception) {
                                id = ""
                            }
                            try {
                                photoURL = response.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail").toString()
                            } catch (e : Exception) {
                                photoURL = ""
                            }
                            try {
                                author = "By ${response.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("authors").getString(0).toString()}"
                            } catch (e : Exception) {
                                author = ""
                            }
                            try {
                                description = response.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("description").toString()
                            } catch (e : Exception) {
                                description = ""
                            }
                            try {
                                title = response.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("title").toString()
                            } catch (e : Exception) {
                                title = ""
                            }
                            if (photoURL != "") {
                                val ar = photoURL.split("http")
                                photoURL = "https${ar[1]}"
                            }
                            bookList.add(Book(title, author, photoURL, id, description))
                        }
                        val textView : TextView = findViewById(R.id.count)
                        textView.text = "Results ($count books) - "
                        val adapter : BookSearchAdapter = BookSearchAdapter(bookList)
                        book_recycle_view.adapter = adapter
                    },
                    Response.ErrorListener { error ->
                        println(error.localizedMessage)
                    }
            )
            queue?.add(jsonObjectRequest)
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    override fun onStop() {
        super.onStop()
        queue?.cancelAll(TAG)
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
            R.id.search -> {
                val intent = Intent(this, SearchUserActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

}
