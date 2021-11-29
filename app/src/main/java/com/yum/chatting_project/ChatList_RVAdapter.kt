package com.yum.chatting_project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatList_RVAdapter(val items: MutableList<ChatListData>, val context: Context, val chatroomkeys: MutableList<String>) :
    RecyclerView.Adapter<ChatList_RVAdapter.ViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatList_RVAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_list_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatList_RVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position], chatroomkeys[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ChatListData, chatroomkey: String) {
            val title = itemView.findViewById<TextView>(R.id.chat_list_rv_title_textView)
            title.setText(item.title)

            itemView.setOnClickListener {
                Intent(context,MainActivity::class.java).apply {
                    putExtra("채팅방키",chatroomkey)
                }.run { context.startActivity(this) }

                //(임시) 채팅방 클릭 시 그 채팅방에 참여하는 걸로 간주!
                auth = Firebase.auth
                database = Firebase.database.reference
                val current_uid = auth.currentUser!!.uid.toString()
                database.child("ChatRooms").child(chatroomkey!!).child("users").child(current_uid).setValue(true)
            }
        }
    }
}