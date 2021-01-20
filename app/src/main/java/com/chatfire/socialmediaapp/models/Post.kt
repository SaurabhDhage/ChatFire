package com.chatfire.socialmediaapp.models

data class Post(
        val text: String = "",
        val createdBy: User = User(),
        val createdAt: Long = 0L,
        val likedBy: ArrayList<String>?)
{
    constructor() : this("", User(),0L, ArrayList())

}


