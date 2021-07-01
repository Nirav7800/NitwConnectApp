package com.example.nitwconnect.daos

import android.provider.Settings
import com.example.nitwconnect.models.Post
import com.example.nitwconnect.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db=FirebaseFirestore.getInstance()
    val postCollection=db.collection("posts")
    val auth=Firebase.auth
    fun addPost(text:String)
    {
        val current_uid=auth.currentUser!!.uid

        GlobalScope.launch(Dispatchers.IO){
            val userDao=UserDao()
            val user=userDao.getUserById(current_uid).await().toObject(User::class.java)!!
            val current_time=System.currentTimeMillis()
            val post= Post(text,user,current_time)
            postCollection.document().set(post)
        }
    }


    fun getPostById(postId:String): Task<DocumentSnapshot>
    {
        return postCollection.document(postId).get()

    }
    fun updateLikes(postId:String){

        GlobalScope.launch(Dispatchers.IO) {
            val current_uid=auth.currentUser!!.uid

            val post=getPostById(postId).await().toObject(Post::class.java)!!
         val isLiked=   post.likedBy.contains(current_uid)

            if(isLiked){
              post.likedBy.remove(current_uid)
            }
            else
            {
              post.likedBy.add(current_uid)
            }
            postCollection.document(postId).set(post)
        }


    }
}