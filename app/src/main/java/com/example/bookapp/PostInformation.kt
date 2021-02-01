package com.example.bookapp

data class PostInformation(val text : String = "", val user : User = User(), val time : Long = 0L, val likedBy: ArrayList<String> = ArrayList())
