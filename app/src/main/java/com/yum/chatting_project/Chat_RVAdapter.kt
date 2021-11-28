package com.yum.chatting_project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Chat_RVAdapter(val items: MutableList<ChatData>, val context: Context) :
    RecyclerView.Adapter<Chat_RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Chat_RVAdapter.ViewHolder {
        //뷰홀더를 생성(레이아웃 생성)하는 코드 작성
        val view = LayoutInflater.from(context).inflate(R.layout.chat_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Chat_RVAdapter.ViewHolder, position: Int) {
        //뷰홀더가 재활용될때 실행되는 메소드 작성
        holder.bindItems(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ChatData) {
            val rv_nickname = itemView.findViewById<TextView>(R.id.rv_nickname_textView)
            rv_nickname.text = item.nickname

            val rv_msg = itemView.findViewById<TextView>(R.id.rv_msg_textView)
            rv_msg.text = item.msg

            val rv_profile_btn = itemView.findViewById<Button>(R.id.rv_profile_btn)
            rv_profile_btn.setOnClickListener {
                Intent(context, ProfileActivity::class.java).apply {
                    putExtra("이메일", item.email)
                    putExtra("닉네임", item.nickname)
                }.run { context.startActivity(this) }
            }

        }
    }
}