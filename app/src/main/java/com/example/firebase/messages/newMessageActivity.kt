package com.example.firebase.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebase.R
import com.example.firebase.model.User

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_msg.view.*

class newMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"


        val adapter = GroupAdapter<GroupieViewHolder>()


        recyclerview_new_msg.adapter = adapter
        fetchUsers()

    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUsers(){
        val ref =FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null){
                      adapter.add(userItem(user))
                    }
                    adapter.setOnItemClickListener { item, view ->
                        val intent = Intent(view.context,ChatLogActivity::class.java)
                        val userItem = item as userItem
                        intent.putExtra(USER_KEY,userItem.user)

                        startActivity(intent)
                        finish()
                    }
                }
                recyclerview_new_msg.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }
}
class userItem(val user: User):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_new_msg
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.usernaem.text = user.username
        Picasso.get().load(user.imageUrl).into(viewHolder.itemView.profileimage)

    }

}


