package com.yum.chatting_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //이메일 로그인 작업위한 변수
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_include_drawer)


        //채팅방 데이터베이스 하위에 채팅 데이터 저장하기
        database = Firebase.database.reference
        auth = Firebase.auth

        val current_email = auth.currentUser!!.email.toString()
        val current_uid = auth.currentUser!!.uid.toString()

        //채팅방 키 값 받아옴
        val intent = getIntent()
        val chatroomkey = intent.getStringExtra("채팅방키");

        val return_intent = Intent(this, ChatListActivity::class.java)

        //버튼을 눌러 메뉴를 오픈할 수도 있고, 왼쪽에서 오른쪽으로 스왑해 오픈할 수 있습니다.
        //DrawerLayout의 id에 직접 openDrawer()메소드를 사용할 수 있습니다.
        findViewById<Button>(R.id.menu_btn).setOnClickListener {
            findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.END)
        }

        //채팅방 나가기
        findViewById<Button>(R.id.btn_exit_room).setOnClickListener {

            //chatRooms에서 사용자 삭제
            database.child("ChatRooms").child(chatroomkey!!).child("users").child(current_uid)
                .removeValue()
            //UserRooms에서 채팅방 키 삭제하여 해당 유저의 화면에서 안보이게함
            database.child("UserRooms").child(current_uid).removeValue()
            //채팅방에 유저 아무도 없으면 파이어베이스에서 채팅방 데이터 삭제
            database.child("ChatRooms").child(chatroomkey!!).child("users").get().addOnSuccessListener {
                if(it.value == null){
                    database.child("ChatRooms").child(chatroomkey!!).removeValue()
                }
            }
        }


        //리사이클러뷰에 들어갈 아이템 추가
        val chats = mutableListOf<ChatData>()
        val roomusers = mutableListOf<String>()

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        val rvAdapter = Chat_RVAdapter(chats, this)
        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //채팅방 참여자 리사이클러뷰(드로어메뉴)
        val roomUser_rv = findViewById<RecyclerView>(R.id.rv_room_user_list)
        val roomuserRvadapter = RoomUser_RVAdapter(roomusers, this)
        roomUser_rv.adapter = roomuserRvadapter
        roomUser_rv.layoutManager = LinearLayoutManager(this)

        //채팅 보낼때 감지
        database.child("ChatRooms").child(chatroomkey!!).child("messages").addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Log.d("데이터", "추가됨")
                    val saved_email = snapshot.getValue<ChatData>()!!.email
                    val saved_uid = snapshot.getValue<ChatData>()!!.uid
                    //내 닉네임은 "나", 상대방 닉네임은 이메일
                    if (saved_uid == current_uid) {
                        chats.add(
                            ChatData(
                                "나",
                                snapshot.getValue<ChatData>()!!.msg,
                                saved_email, saved_uid
                            )
                        )
                        //Log.d("데이터", items.toString())
                    } else {
                        chats.add(
                            ChatData(
                                saved_email,
                                snapshot.getValue<ChatData>()!!.msg,
                                saved_email, saved_uid
                            )
                        )
                    }
                    //items에 변화가 생기면 반영
                    rvAdapter.notifyDataSetChanged()
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

        //채팅방 참여자 업데이트 감지
        database.child("ChatRooms").child(chatroomkey!!).child("users")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("추가됨", snapshot.key.toString())

                    //참여자의 회원정보 가져오기
                    val userQuery =
                        database.child("Users").orderByKey().equalTo(snapshot.key.toString())
                    userQuery.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            val nickname = snapshot.getValue<UserData>()!!.nickname
                            roomusers.add(nickname)
                            Log.d("추가됨_닉네임", nickname)
                            roomuserRvadapter.notifyDataSetChanged()
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            TODO("Not yet implemented")
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            TODO("Not yet implemented")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    //roomusers.add("사용자이름")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("업데이트됨", snapshot.toString())
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    startActivity(return_intent)
                    finish()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("업데이트됨", snapshot.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("업데이트됨", error.toString())
                }

            })

        //채팅방 제목 가져오기
        //데이터 한번만 가져올때는 get()함수 사용
        database.child("ChatRooms").child(chatroomkey!!).get().addOnSuccessListener {
            //Log.d("뭘까", it.child("title").value.toString())
            val title = it.child("title").value.toString()
            findViewById<TextView>(R.id.tv_chatroom_title).setText(title)
        }

        //채팅 보낼시 이벤트
        val sendBtn = findViewById<Button>(R.id.btn_chat_send)
        sendBtn.setOnClickListener {
            val message = findViewById<EditText>(R.id.editText_chat_msg)
            val chatData =
                ChatData(current_email, message.text.toString(), current_email, current_uid)
            //myRef.push().setValue(chatData)
            database.child("ChatRooms").child(chatroomkey!!).child("messages").push().setValue(chatData)
            message.setText("")


            //키보드 내리기
            val manager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        //1. 이메일 기반 회원가입 O
        //2. 채팅 메시지 한 곳에 저장 O
        //3. 그 메시지를 누가 보냈는지에 따라 닉네임 다르게 O
        //4. 로그인 화면 다음에 닉네임 설정 화면 나오게
        //5. 채팅방에서 상대방 프로필 클릭하면 닉네임, 이메일 보이게 (Intent 전달할때 값 같이 전달)

    }
}