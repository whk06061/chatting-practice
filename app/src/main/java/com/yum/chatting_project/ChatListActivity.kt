package com.yum.chatting_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChatListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        //리사이클러뷰에 들어갈 아이템 추가
        val items = mutableListOf<ChatListData>()
        val chatroomkeys = mutableListOf<String>()

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.chat_list_rv)
        //채팅방 클릭시 해당 채팅방 하위에 데이터베이스 생성하기 위해 키값 넘겨줌
        val rvAdapter = ChatList_RVAdapter(items, this, chatroomkeys)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)
        //아이템 사이 구분선 추가
        rv.addItemDecoration(DividerItemDecoration(this,1))

        //1. 파이어베이스에서 채팅방 불러오기
        database = Firebase.database.reference
        database.child("ChatRooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                chatroomkeys.clear()
                for (dataModel in snapshot.children) {
                    Log.d("아이템1", dataModel.toString())
                    items.add(dataModel.getValue(ChatListData::class.java)!!)
                    //채팅방 고유 키 저장
                    chatroomkeys.add(dataModel.key!!)
                }
                //items에 변화가 생기면 반영
                rvAdapter.notifyDataSetChanged()
                //Log.d("키", chatroomkeys.toString())
                //Log.d("아이템2", items.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )


        //2. 채팅방 생성하기
        val create_chatroom_btn = findViewById<Button>(R.id.btn_create_chatroom)
        create_chatroom_btn.setOnClickListener {

            //다이얼로그 띄우기
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.create_chat_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("채팅방 생성하기")

            val mAlertDialog = mBuilder.show()
            val create_btn = mAlertDialog.findViewById<Button>(R.id.dialog_btn_create)
            create_btn!!.setOnClickListener {
                val chatroom_title =
                    mAlertDialog.findViewById<EditText>(R.id.dialog_editText_chatroom_name)
                val chatListData = ChatListData(chatroom_title!!.text.toString())
                database.child("ChatRooms").push().setValue(chatListData)

                //다이얼로그 사라지게
                mAlertDialog.dismiss()
            }
        }

    }
}