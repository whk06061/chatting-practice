package com.yum.chatting_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //이메일 로그인 작업위한 변수
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        val email = auth.currentUser!!.email.toString()
        //파이어베이스에 데이터 쓰기위한 변수
        val database = Firebase.database
        val myRef = database.getReference("chatting")

        //리사이클러뷰에 들어갈 아이템 추가
        val items = mutableListOf<ChatData>()

        //리사이클러뷰 어댑터 연결
        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        val rvAdapter = RVAdapter(items, this)

        //파이어베이스에 데이터가 업데이트 될때마다 실행
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (dataModel in snapshot.children) {
                    Log.d("데이터", dataModel.toString())
                    val saved_email = dataModel.getValue<ChatData>()!!.nickname
                    //내 닉네임은 "나", 상대방 닉네임은 이메일
                    if (saved_email == email) {
                        items.add(ChatData("나", dataModel.getValue<ChatData>()!!.msg, saved_email))
                    } else {
                        items.add(
                            ChatData(
                                dataModel.getValue<ChatData>()!!.email,
                                dataModel.getValue<ChatData>()!!.msg,
                                saved_email
                            )
                        )
                    }
                }
                rvAdapter.notifyDataSetChanged() //items에 변화가 생기면 반영
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //Log.d("회원정보", auth.currentUser!!.uid)

        //        items.add(ChatData("햄찌","안녕하세요~!"))
        //        items.add(ChatData("냥냥이","엇 안녕하세요~!"))
        //        items.add(ChatData("햄찌","만나서 반갑습니다!"))
        //        items.add(ChatData("냥냥이","저두요"))


        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(this)


        val sendBtn = findViewById<Button>(R.id.btn_chat_send)
        sendBtn.setOnClickListener {
            val message = findViewById<EditText>(R.id.editText_chat_msg)
            val chatData = ChatData(email, message.text.toString(),email)
            myRef.push().setValue(chatData)
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