package com.example.bookapp

data class Book(val title : String = "", val author : String = "", val photoURL : String = "", val id : String = "", val description: String = "")

//class Book(private val title : String, private val author : String, private val photoURL : String, private val id : String, private val description: String) {
//    fun getTitle() : String {
//        return title
//    }
//    fun getAuthor() : String {
//        return author
//    }
//    fun getPhotoUrl() : String {
//        return photoURL
//    }
//    fun getId() : String {
//        return id
//    }
//    fun getDescription() : String {
//        return description
//    }
//}