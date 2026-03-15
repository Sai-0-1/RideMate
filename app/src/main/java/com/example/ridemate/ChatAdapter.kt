package com.example.ridemate

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>, private val currentUserId: String) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageContainer: LinearLayout = view.findViewById(R.id.llMessageContainer)
        val messageText: TextView = view.findViewById(R.id.tvMessageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text

        val layoutParams = holder.messageContainer.layoutParams as LinearLayout.LayoutParams
        if (message.senderId == currentUserId) {
            layoutParams.gravity = Gravity.END
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_sent)
        } else {
            layoutParams.gravity = Gravity.START
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_received)
        }
        holder.messageContainer.layoutParams = layoutParams
    }

    override fun getItemCount() = messages.size
}