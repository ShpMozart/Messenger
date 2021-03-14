package com.example.firebase.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.firebase.R
import com.example.firebase.model.User
import com.example.firebase.model.chatMessages
import com.example.firebase.registration.RegisterActivity
import com.example.firebase.view.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.util.*
import kotlin.collections.HashMap

class LatestMessagesActivity : AppCompatActivity() {
    companion object{
        var currentUser: User? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        latestMessageRecyclerView.adapter = adapter
        latestMessageRecyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this,ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(newMessageActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }
        listenforLatestMessages()
        fetchCurrentUser()
        verifyUserLoggedin()
    }
    private val latestMessageMap=HashMap<String,chatMessages>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun listenforLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessages = snapshot.getValue(chatMessages::class.java) ?: return

                latestMessageMap[snapshot.key!!] = chatMessages
                refreshRecyclerViewMessages()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessages = snapshot.getValue(chatMessages::class.java) ?: return

                latestMessageMap[snapshot.key!!] = chatMessages
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    private fun fetchCurrentUser(){
       val uid= FirebaseAuth.getInstance().uid
       val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

        })
    }
    private fun verifyUserLoggedin(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
   }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.menu_sign_out -> {
               FirebaseAuth.getInstance().signOut()
               val intent = Intent(this, RegisterActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)


           }
           R.id.new_message ->{
               val intent=Intent(this, newMessageActivity::class.java)
               startActivity(intent)

           }
       }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

}