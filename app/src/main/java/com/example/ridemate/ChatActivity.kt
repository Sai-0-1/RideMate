package com.example.ridemate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private var rideId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        rideId = intent.getStringExtra("rideId") ?: ""
        database = FirebaseDatabase.getInstance().getReference("chats/$rideId/messages")

        recyclerView = findViewById(R.id.rvMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(messageList, auth.currentUser!!.uid)
        recyclerView.adapter = chatAdapter

        val msgET = findViewById<EditText>(R.id.etMessage)
        val sendBtn = findViewById<Button>(R.id.btnSend)

        sendBtn.setOnClickListener {
            val text = msgET.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            val msg = hashMapOf(
                "senderId" to auth.currentUser!!.uid,
                "text" to text,
                "timestamp" to System.currentTimeMillis()
            )
            database.push().setValue(msg)
            msgET.setText("")
        }

        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                val senderId = snapshot.child("senderId").getValue(String::class.java) ?: ""
                val text = snapshot.child("text").getValue(String::class.java) ?: ""
                messageList.add(ChatMessage(senderId, text))
                chatAdapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
            }
            override fun onChildChanged(s: DataSnapshot, p: String?) {}
            override fun onChildRemoved(s: DataSnapshot) {}
            override fun onChildMoved(s: DataSnapshot, p: String?) {}
            override fun onCancelled(e: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Chat error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class ChatMessage(val senderId: String = "", val text: String = "")