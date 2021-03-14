package com.example.firebase.messages

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.model.User
import com.example.firebase.model.chatMessages
import com.example.firebase.view.ChatFromItem
import com.example.firebase.view.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        chatMsgRecyclerView.adapter = adapter

         toUser = intent.getParcelableExtra<User>(newMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username


        listenForMessages()


        sendMsgButton.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")

            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(chatMessages::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser?:return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    } else {

                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }
                chatMsgRecyclerView.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = chatTextView.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(newMessageActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return
        if (toId == null) return


//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()



        val chatMessage =
            chatMessages(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                chatTextView.text.clear()
                chatMsgRecyclerView.scrollToPosition(adapter.itemCount - 1)

            }
        toReference.setValue(chatMessage)
        val LatestMessagerefrence = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId")
        LatestMessagerefrence.setValue(chatMessage)
        ///////////////
        val LatestMessageTorefrence = FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
        LatestMessageTorefrence.setValue(chatMessage)

    }
}




