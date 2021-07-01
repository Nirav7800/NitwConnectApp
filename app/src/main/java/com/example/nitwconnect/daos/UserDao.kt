package com.example.nitwconnect.daos

import com.example.nitwconnect.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    private val db=FirebaseFirestore.getInstance()
    private val userscollection=db.collection("users")

    fun adduser(user: User?)
    {
             user?.let {
                 GlobalScope.launch(Dispatchers.IO) {
                     userscollection.document(user.uid).set(it)
                 }

             }
    }

    fun getUserById(Id:String): Task<DocumentSnapshot> {
        return userscollection.document(Id).get()
    }
}