package com.example.socialmediaapp


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.daos.PostDao
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.example.socialmediaapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var postDao: PostDao
    private lateinit var adapter:PostAdapter
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fab.setOnClickListener {
            val intent=Intent(this, CreatePostActivity::class.java)
            startActivity(intent)

        }


        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.logout)
        {
            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage(R.string.msg)
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                R.string.logout
            ) { dialog, id -> dialog.cancel()
                Firebase.auth.signOut()
                val intent=Intent(this,SignInActivity::class.java)
                startActivity(intent)
                finish()
            }

            builder1.setNegativeButton(
                R.string.cancel
            ) { dialog, id -> dialog.cancel() }

            val alert11 = builder1.create()
            alert11.show()

        }
        return super.onOptionsItemSelected(item)
    }


    private fun setUpRecyclerView() {

        postDao= PostDao()
        val postCollection=postDao.postCollection
        val query=postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOption=FirestoreRecyclerOptions.Builder<Post>().setQuery(
            query,
            Post::class.java
        ).build()
        adapter=PostAdapter(recyclerViewOption, this)
        binding.rcView.adapter=adapter
        binding.rcView.layoutManager=LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
    adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
    adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {

        postDao.updateLikes(postId)
    }


}

