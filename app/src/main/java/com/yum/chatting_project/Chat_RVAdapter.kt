package com.yum.chatting_project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Chat_RVAdapter(val items: MutableList<ChatData>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var auth: FirebaseAuth
    val RIGHT_TALK = 0
    val LEFT_TALK = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //뷰홀더를 생성(레이아웃 생성)하는 코드 작성
        return when(viewType){
            RIGHT_TALK ->{
                val view = LayoutInflater.from(context).inflate(R.layout.chat_rv_item_me, parent, false)
                RightViewHolder(view)
            }
            LEFT_TALK ->{
                val view = LayoutInflater.from(context).inflate(R.layout.chat_rv_item, parent, false)
                LeftViewHolder(view)
            } else ->{
                val view = LayoutInflater.from(context).inflate(R.layout.chat_rv_item, parent, false)
                LeftViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //뷰홀더가 재활용될때 실행되는 메소드 작성
       if(holder is LeftViewHolder){
           holder.bindItems(items[position])
       } else if(holder is RightViewHolder){
           holder.bindItems(items[position])
       }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        auth = Firebase.auth
        val current_uid = auth.currentUser!!.uid.toString()
        val chatData = items.get(position)
        if(chatData.uid.equals(current_uid)){
            //내 채팅인 경우 0
            return RIGHT_TALK
        }else{
            //다른 사람 채팅인 경우 1
            return LEFT_TALK
        }
    }

    inner class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    inner class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ChatData) {
            val rv_msg = itemView.findViewById<TextView>(R.id.rv_msg_textView)
            rv_msg.text = item.msg

        }
    }
}