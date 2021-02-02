package com.example.bookapp

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    val RC_SIGN_IN : Int = 123
    val TAG : String = "hello"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1031639998127-nor4dd1k10douf3feqh1kqv9qq8mb709.apps.googleusercontent.com")
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val google_image : ImageView = findViewById(R.id.imageView3)
        val google_text : TextView = findViewById(R.id.google_text)
        google_image.setOnClickListener {
            signIn()
        }
        google_text.setOnClickListener {
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            val intent: Intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        val progressBar: ProgressBar = findViewById(R.id.loading)
                        progressBar.visibility = View.VISIBLE
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        // adding user to database
                        val currentUser = mAuth.currentUser
                        val _id = currentUser?.uid
                        if (currentUser != null) {
                            val user = User(currentUser.displayName.toString(), currentUser.uid, currentUser.photoUrl.toString())
                            usersCollection.get()
                                    .addOnSuccessListener {
                                        var contain = false
                                        for (document in it) {
                                            if (document.id == currentUser.uid) contain = true
                                        }
                                        if (!contain) usersCollection.document(currentUser.uid).set(user)
                                    }
                            db.collection("following").get()
                                    .addOnSuccessListener {
                                        var contain = false
                                        for (document in it) {
                                            if (document.id == currentUser.uid) contain = true
                                        }
                                        if (!contain) db.collection("following").document(currentUser.uid).set(Follow())
                                    }
                        }
                        val intent: Intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }

                    // ...
                })
    }

}
