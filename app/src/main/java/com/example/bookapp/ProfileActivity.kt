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
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import kotlin.math.log

class ProfileActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.profile_page
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val profile_photo : ImageView = findViewById(R.id.profile_photo)
        val name : TextView = findViewById(R.id.name)

        var userName = currentUser?.displayName
        if (userName != null && userName.contains(" ")) {
            val arr = userName.split(" ")
            userName = arr[0] + "\n" + arr[1]
        }
        name.text = userName
        Picasso.get().load(mAuth.currentUser?.photoUrl.toString()).into(profile_photo);

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

}