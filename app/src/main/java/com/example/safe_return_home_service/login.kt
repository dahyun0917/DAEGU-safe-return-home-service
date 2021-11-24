package com.example.safe_return_home_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class login: AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    lateinit var btn_log: Button
    lateinit var idEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var autologin: CheckBox
    lateinit var signup : Button
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        auth = FirebaseAuth.getInstance();

        btn_log = findViewById(R.id.btn_log)
        idEditText = findViewById(R.id.idEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        autologin=findViewById(R.id.autologin)
        signup= findViewById(R.id.signup)
        // 회원가입 창으로
        signup.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
        }

        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if(MySharedPreferences.getUserId(this).isNullOrBlank()
            || MySharedPreferences.getUserPass(this).isNullOrBlank()) {
            Login()
        }
        else { // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
            Toast.makeText(this, "${MySharedPreferences.getUserId(this)}님 자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            moveMainPage(auth?.currentUser)
        }


    }

    // 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    /*public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }*/

    private fun Login(){
        // 로그인 버튼
        btn_log.setOnClickListener {
            if(idEditText.text.isNullOrBlank() || passwordEditText.text.isNullOrBlank()) {
                Toast.makeText(this, "아이디와 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }
            else if(count==1){
                MySharedPreferences.setUserId(this, idEditText.text.toString())
                MySharedPreferences.setUserPass(this, passwordEditText.text.toString())
                signIn(idEditText.text.toString(),passwordEditText.text.toString())
            }
            else {
                signIn(idEditText.text.toString(), passwordEditText.text.toString())
            }
        }
        autologin.setOnCheckedChangeListener { compoundButton, b ->
            if(autologin.isChecked==true)
                count=1
            else
                count=0
        }
    }
    // 로그인
    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "로그인에 실패 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?){
        if( user!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            count = data!!.getIntExtra("Count",0)
            Toast.makeText(applicationContext,"count: $count",Toast.LENGTH_SHORT).show()
        }
    }*/
}