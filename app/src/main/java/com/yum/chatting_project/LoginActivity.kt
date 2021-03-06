package com.yum.chatting_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        database = Firebase.database.reference

        val loginBtn = findViewById<Button>(R.id.btn_login).setOnClickListener {

            val email = findViewById<EditText>(R.id.editText_login_email)
            val password = findViewById<EditText>(R.id.editText_login_password)

            //로그인
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this,ChatListActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this,"로그인 성공!",Toast.LENGTH_LONG).show()
                        //Log.d("회원정보", auth.currentUser!!.uid)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this,"로그인 실패!",Toast.LENGTH_LONG).show()
                    }
                }
        }

        val joinBtn = findViewById<Button>(R.id.btn_join).setOnClickListener {
            val email = findViewById<EditText>(R.id.editText_login_email)
            val email_text = email.text.toString()
            val password = findViewById<EditText>(R.id.editText_login_password)
            val password_text = password.text.toString()

            //회원가입
            auth.createUserWithEmailAndPassword(email_text, password_text)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this,"회원가입 성공!",Toast.LENGTH_LONG).show()
                        email.setText("")
                        password.setText("")
                        val userData = UserData(email_text,email_text)
                        database.child("Users").child(auth.currentUser!!.uid).setValue(userData)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this,"회원가입 실패!",Toast.LENGTH_LONG).show()
                        email.setText("")
                        password.setText("")

                    }
                }
        }

    }
}