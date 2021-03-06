package com.example.nitwconnect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nitwconnect.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class PostAdapter(options: FirestoreRecyclerOptions<Post>,val listner:IPostAdapter) :FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder>(
    options
) {

    class PostViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val deleteButton:ImageView=itemView.findViewById(R.id.deletebtn)
        val shareButton:ImageView=itemView.findViewById(R.id.sharebtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val viewHolder= PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))

        viewHolder.likeButton.setOnClickListener {
             listner.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.deleteButton.setOnClickListener {

            listner.onDeleteClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.shareButton.setOnClickListener {
            listner.onShareClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageurl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text=Utils.getTimeAgo(model.createdAt)

        val auth= Firebase.auth

        val currentUserId=auth.currentUser!!.uid

        val isLiked=  model.likedBy.contains(currentUserId)
        if(isLiked) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_baseline_favorite_24))
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_baseline_favorite_border_24))
        }

    }


}

interface IPostAdapter{
    fun onLikeClicked(postId:String)
    fun onDeleteClicked(postId: String)
    fun onShareClicked(postId: String)


}
