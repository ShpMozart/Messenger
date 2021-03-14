package com.example.firebase.registration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.firebase.R
import com.example.firebase.messages.LatestMessagesActivity
import com.example.firebase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


private lateinit var auth: FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth


        already_have_an_account_textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
            register_button.setOnClickListener {
                performListener()


            }
        profilePicture_button.setOnClickListener {
            Toast.makeText(this,"PROFILE",Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
            Log.i("check997",uri.toString())
        }
    }
    var uri:Uri ?= null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
             uri = data.data
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(profilePicture_button)
            Log.i("check998",uri.toString())
          ////////////////////////////////////
        }
    }
    private fun performListener(){
        val email = emailTextView.text.toString()
        val password = passwordTextView.text.toString()
        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this,"EMAIL OR PASSWORD IS EMPTY",Toast.LENGTH_LONG).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    uploadImageTofirebase()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    val storage = Firebase.storage("gs://kotlin-firebase-b1b2f.appspot.com/")
    private fun uploadImageTofirebase(){
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/${filename}")
        Log.i("check999",uri.toString())
        ref.putFile(uri!!)
            .addOnSuccessListener {

                Log.i("image_task", "done: ${it.metadata?.path}")


                ref.downloadUrl.addOnSuccessListener {

                    Log.i("image_url", "done$it")

                    saveUserToFirebaseDataBase(it.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                ///////////////
            }
    }
   private fun saveUserToFirebaseDataBase(imageUrl: String)
   {
       val uid = FirebaseAuth.getInstance().uid?:""
       Toast.makeText(this,uid.toString(),Toast.LENGTH_LONG).show()
       var ref = FirebaseDatabase.getInstance("https://kotlin-firebase-b1b2f-default-rtdb.firebaseio.com/").getReference("/users/$uid")
       val user = User(
           uid,
           usernameTextView.text.toString(),
           imageUrl
       )
       ref.setValue(user)
           .addOnSuccessListener {
               Log.i("db999","ok shod")

               val intent =Intent(this,
                   LatestMessagesActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)

           }
           .addOnFailureListener {
               Log.i("fail999",it.message.toString())
               Toast.makeText(this,"error",Toast.LENGTH_LONG).show()
           }


   }
}




