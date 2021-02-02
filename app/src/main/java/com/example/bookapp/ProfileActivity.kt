package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import kotlin.math.log
import kotlin.math.min

class ProfileActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    val followingCollecion = db.collection("following")
    lateinit var show_post: TextView
    lateinit var show_following: TextView
    lateinit var show_followers: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("My Profile");

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val logout : Button = findViewById(R.id.logout_button)
        logout.setOnClickListener {
            val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            mAuth.signOut()
            googleSignInClient.signOut()
            val intent : Intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        show_post = findViewById(R.id.show_post)
        show_following = findViewById(R.id.show_followings)
        show_followers = findViewById(R.id.show_followers)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.profile_page
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val profile_photo : ImageView = findViewById(R.id.user_image)
        val name : TextView = findViewById(R.id.name)

        var userName = currentUser?.displayName
        name.text = userName
        Picasso.get().load(mAuth.currentUser?.photoUrl.toString()).into(profile_photo);

        val more: TextView = findViewById(R.id.more)

        more.setOnClickListener {
            val intent = Intent(this, ShowCollectonActivity::class.java)
            intent.putExtra("user_id", mAuth.currentUser!!.uid)
            startActivity(intent)
        }

        show_posts()
        show_following()
        show_followers()
        show_books()

        show_post.setOnClickListener {
            val intent = Intent(this, ShowPostActivity::class.java)
            intent.putExtra("id", mAuth.currentUser!!.uid)
            startActivity(intent)
        }

        show_followers.setOnClickListener {
            val intent = Intent(this, ShowFollowersActivity::class.java)
            intent.putExtra("id", mAuth.currentUser!!.uid)
            startActivity(intent)
        }

        show_following.setOnClickListener {
            val intent = Intent(this, ShowFollowingActivity::class.java)
            intent.putExtra("id", mAuth.currentUser!!.uid)
            startActivity(intent)
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
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
            R.id.search -> {
                val intent = Intent(this, SearchUserActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun show_books() {
        db.collection("users").document(mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject(User::class.java)!!
                    val imageViewArr : ArrayList<Int> = ArrayList()
                    imageViewArr.add(R.id.imageView1)
                    imageViewArr.add(R.id.imageView2)
                    imageViewArr.add(R.id.imageView3)
                    if (!user.bookList.isEmpty()) {
                        for (i in 0..min(2, user.bookList.size-1)) {
                            val imageView : ImageView = findViewById(imageViewArr[i])
                            Picasso.get().load(user.bookList[i].photoURL.toString()).into(imageView)
                        }
                    }
                }
            }
    }

    fun show_following() {
        followingCollecion.document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val data = it.toObject(Follow::class.java)!!
                show_following.text = "${data.follow_list.size}\nFollowing"
            }
        }
    }

    fun show_followers() {
        followingCollecion.get()
            .addOnSuccessListener { result->
                var followers_count = 0
                for (document in result) {
                    val data = document.data["follow_list"] as ArrayList<*>
                    for (i in data) {
                        if (i.toString() == mAuth.currentUser!!.uid) followers_count++
                    }
                }
                show_followers.text = "$followers_count\nFollowers"
            }
    }

    fun show_posts() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                var post_count = 0
                for (document in result) {
                    val i = document.data["user"] as Map<*, *>
                    if (i["id"] == mAuth.currentUser!!.uid) post_count++
                }
                show_post.text = "$post_count\nPost"
            }
    }

}