package com.yum.chatting_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val intent = getIntent()
        val email = intent.getStringExtra("이메일");
        val nickname = intent.getStringExtra("닉네임");
//        Log.d("전달받은거", email!!)
        findViewById<TextView>(R.id.profile_email).setText(email)
        findViewById<TextView>(R.id.profile_nickname).setText(nickname)
    }
}