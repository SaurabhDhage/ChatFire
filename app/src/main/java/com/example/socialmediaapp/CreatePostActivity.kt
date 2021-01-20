package com.example.socialmediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialmediaapp.daos.PostDao
import com.example.socialmediaapp.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var postDao:PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding =ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao= PostDao()
        binding.submitPost.setOnClickListener {
            val input=binding.postInput.text.toString().trim()
            if(input.isNotEmpty())
            {
                postDao.addPost(input)
                finish()
            }

        }
    }
}