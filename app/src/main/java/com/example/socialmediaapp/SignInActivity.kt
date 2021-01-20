package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.socialmediaapp.daos.UserDao

import com.example.socialmediaapp.databinding.ActivitySignInBinding
import com.example.socialmediaapp.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int=123
    private lateinit var  binding:ActivitySignInBinding
    private lateinit var googleSignInClient:GoogleSignInClient
    private val TAG="SignIn Activity"
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)
        auth=Firebase.auth

        binding.signInBtn.setOnClickListener { 
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
       startActivityForResult(signInIntent, RC_SIGN_IN)


    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        }


    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account =
                    task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            // ...
        }

    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.signInBtn.visibility=View.GONE
        binding.pgBar.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO)
        {
            val auth=auth.signInWithCredential(credential).await()
            val firebaseUser=auth.user
            withContext(Dispatchers.Main)
            {
                updateUI(firebaseUser)
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
       if(firebaseUser!=null)
       {
           val user=User(firebaseUser.uid,firebaseUser.displayName,
               firebaseUser.photoUrl.toString()
           )
           val userDao=UserDao()
           userDao.addUser(user)

           val intent=Intent(this,MainActivity::class.java)
           startActivity(intent)
           finish()
       }
        else
       {
           binding.signInBtn.visibility=View.VISIBLE
           binding.pgBar.visibility=View.GONE
       }
    }
}