package com.yum.chatting_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChatListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        auth = Firebase.auth
        val current_uid = auth.currentUser!!.uid

        //리사이클러뷰에 들어갈 아이템 추가
        val items = mutableListOf<ChatListData>()
        val chatroomkeys = mutableListOf<String>()
        val users = mutableListOf<String>()

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.chat_list_rv)
        //채팅방 클릭시 해당 채팅방 하위에 데이터베이스 생성하기 위해 키값 넘겨줌
        val rvAdapter = ChatList_RVAdapter(items, this, chatroomkeys)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)
        //아이템 사이 구분선 추가
        rv.addItemDecoration(DividerItemDecoration(this, 1))

        //1. 파이어베이스에서 채팅방 불러오기
        database = Firebase.database.reference

        //사용자가 참여한 채팅방만 보여줌
        database.child("UserRooms").child(current_uid)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    //사용자가 참여한 채팅방을 리사이클러뷰에 추가
                    database.child("ChatRooms").child(snapshot.value.toString()).get().addOnSuccessListener {
                        val key = it.key
                        items.add(it.getValue(ChatListData::class.java)!!)
                        //채팅방 고유 키 저장
                        chatroomkeys.add(key!!)
                        rvAdapter.notifyDataSetChanged()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val key = snapshot.key
                    Log.d("키다", key.toString())
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val key = snapshot.key
                    Log.d("키다", key.toString())
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    val key = snapshot.key
                    Log.d("키다", key.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("키다", error.toString())
                }

            }
            )


        //가입된 회원들 정보 가져오기
        database.child("Users").addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("아이템1", snapshot.toString())
                users.add(snapshot.key.toString())

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
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

                //임시로!!!!!!! 채팅방 생성하면 모든 회원 참여시킴
                var chatroomKey = database.child("ChatRooms").push().key
                database.child("ChatRooms").child(chatroomKey!!).setValue(chatListData)
                for (user in users) {
                    database.child("ChatRooms").child(chatroomKey!!).child("users").child(user)
                        .setValue(true)
                    //각 사용자가 무슨 채팅방에 참여하고 있는지 저장
                    database.child("UserRooms").child(user).push().setValue(chatroomKey)
                }

                //다이얼로그 사라지게
                mAlertDialog.dismiss()
            }
        }

    }
}