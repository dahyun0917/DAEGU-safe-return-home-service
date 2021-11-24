package com.example.safe_return_home_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class setting: AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    lateinit var btn_logout : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        auth = Firebase.auth

        btn_logout=findViewById(R.id.btn_logout)
        // 로그아웃

        btn_logout.setOnClickListener {
            MySharedPreferences.clearUser(this)
            /*val intent = Intent(this, login::class.java)
            startActivity(intent)
            rintent.putExtra("Count",0)
            setResult(Activity.RESULT_OK,rintent)
            auth?.signOut()*/
            val intent = Intent(this, login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth?.signOut()

        }
    }
}