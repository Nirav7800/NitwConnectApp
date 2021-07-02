package com.example.nitwconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nitwconnect.daos.PostDao
import com.example.nitwconnect.models.Post
import com.example.nitwconnect.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        floatingActionButton.setOnClickListener {
                  val intent= Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
          // finish()
        }



        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signoutbtn-> {

               FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,SigninActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.sharebutton->{
                val intent= Intent(Intent.ACTION_SEND)
                intent.type="text/plain"
                intent.putExtra(Intent.EXTRA_TEXT,"Hey Checkout This Amazing Web i have got from playstore https://github.com/Nirav7800 ")
                val chooser= Intent.createChooser(intent,"Share This App Using ")
                startActivity(chooser)
            }
          R.id.refreshbtn->{

              val intent=Intent(this,MainActivity::class.java)
              startActivity(intent)
              finish()
          }
            R.id.searchbutton->{



                val searchView=item.actionView as SearchView
                item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
                    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                   displaySearch()

                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {

                        displaySearch()
                        return true
                    }


                })


                searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {


                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {

                        if(!newText.isNullOrEmpty())
                            displaySearch(newText)

                        return false
                    }


                })



            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun displaySearch(newText: String=" ") {

        postDao = PostDao()
        val postsCollections = postDao.postCollection

        val query = postsCollections.orderBy("text").startAt(newText).endAt(newText+"\uf8ff");
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,
            Post::class.java).build()

        adapter= PostAdapter(recyclerViewOptions,this)
     adapter.startListening()

        recyclerView.adapter=adapter
    recyclerView.layoutManager=LinearLayoutManager(this)




    }


    private fun setUpRecyclerView() {

        postDao = PostDao()
        val postsCollections = postDao.postCollection
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter= PostAdapter(recyclerViewOptions,this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }


    override fun onLikeClicked(postId: String) {
           postDao.updateLikes(postId)
    }

    override fun onDeleteClicked(postId: String) {
        postDao.deletpost(postId)
    }


    override fun onShareClicked(postId: String) {
      val text= postDao.sharePost(postId).toString()

    }
}