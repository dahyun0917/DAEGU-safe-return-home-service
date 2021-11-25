package com.example.safe_return_home_service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUp: AppCompatActivity() {
    private lateinit var btn_login : Button
    lateinit var idEditText : EditText
    lateinit var passwordEditText : EditText

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        btn_login = findViewById(R.id.btn_log)
        idEditText =findViewById<EditText>(R.id.idEditText)
        passwordEditText=findViewById<EditText>(R.id.passwordEditText)

        auth = Firebase.auth

        btn_login.setOnClickListener {
            createAccount(idEditText.text.toString(),passwordEditText.text.toString())
            val intent = Intent(this,login ::class.java)
            startActivity(intent)
        }
    }
    private fun createAccount(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "계정 생성 완료.",Toast.LENGTH_SHORT).show()
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(this, "계정 생성 실패",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}