package com.example.bookapp

data class User(val name : String = "", val id : String = "", val photoURL : String = "", val bookList : ArrayList<Book> = ArrayList())
