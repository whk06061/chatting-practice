package com.yum.chatting_project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatList_RVAdapter(val items: MutableList<ChatListData>, val context: Context, val chatroomkeys: MutableList<String>) :
    RecyclerView.Adapter<ChatList_RVAdapter.ViewHolder>() {
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
            }
        }
    }
}