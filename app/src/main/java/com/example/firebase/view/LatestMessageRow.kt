package com.example.firebase.view

import com.example.firebase.R
import com.example.firebase.model.User
import com.example.firebase.model.chatMessages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessages: chatMessages) : Item<GroupieViewHolder>(){
    var chatPartnerUser:User?=null
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latestMessage_textview.text = chatMessages.text
        val chatPartner:String
        if (chatMessages.fromId== FirebaseAuth.getInstance().uid){
            chatPartner = chatMessages.toId
        }
        else{
            chatPartner = chatMessages.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartner")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_textview.text = chatPartnerUser?.username
                val target = viewHolder.itemView.profileImageLatestMessage
                Picasso.get().load(chatPartnerUser?.imageUrl).into(target)
            }

        })
    }
}
