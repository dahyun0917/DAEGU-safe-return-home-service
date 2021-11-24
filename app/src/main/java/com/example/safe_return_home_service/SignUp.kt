package com.example.safe_return_home_service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class SignUp: AppCompatActivity() {
    private lateinit var btn_login : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        btn_login = findViewById(R.id.btn_log)
        btn_login.setOnClickListener {
            val intent = Intent(this,login ::class.java)
            startActivity(intent)
        }
    }
}