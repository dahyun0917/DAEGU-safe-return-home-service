package com.example.safe_return_home_service

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignUp: AppCompatActivity() {
    private lateinit var btn_login : Button
    lateinit var idEditText : EditText
    lateinit var passwordEditText : EditText
    lateinit var passwordCheckEditText : EditText
    lateinit var name : EditText
    lateinit var phone_num : EditText
    lateinit var protectorNum : EditText
    private var auth : FirebaseAuth? = null



    var fbFirestore :FirebaseFirestore?=null
    //var fbAuth : FirebaseAuth?=null
    //var aa : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        btn_login = findViewById(R.id.btn_log)
        idEditText =findViewById<EditText>(R.id.idEditText)
        passwordEditText=findViewById<EditText>(R.id.passwordEditText)
        passwordCheckEditText=findViewById(R.id.passwordCheckEditText)
        name=findViewById(R.id.name)
        phone_num=findViewById(R.id.phone_num)
        protectorNum=findViewById(R.id.protectorNum)

        auth = Firebase.auth
        fbFirestore= FirebaseFirestore.getInstance()

        /*val db = FirebaseFirestore.getInstance()
        val city = hashMapOf(
            "name" to "name1",
            "phonenum" to "phone_num1",
            "NOKphone" to "protectorNum1",
            "email_id" to "email_id1"
        )*/


        btn_login.setOnClickListener {
            val name1=name.text.toString()
            val phone_num1=phone_num.text.toString()
            val protectorNum1=protectorNum.text.toString()
            val email_id1=idEditText.text.toString()
            if(passwordEditText.text.toString()==passwordCheckEditText.text.toString()) {
                createAccount(idEditText.text.toString(), passwordEditText.text.toString())
                if(true){
                    var userInfo = ModelFriends()


                    userInfo.email_id="$email_id1"
                    userInfo.NOKphone="$protectorNum1"
                    userInfo.phonenum="$phone_num1"
                    userInfo.name="$name1"


                    fbFirestore?.collection("information")?.document("$email_id1")?.set(userInfo)
                }
                Toast.makeText(this, "{$email_id1}회원가입 완료되었습니다.",Toast.LENGTH_SHORT).show()
                //val intent = Intent(this,login ::class.java)
                //startActivity(intent)
                finish()
            }
            else
                Toast.makeText(this, "비밀번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show()
            /*db.collection("information").document("test")
                .set(city)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }*/

        }
    }
    private fun createAccount(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "계정 생성 완료.",Toast.LENGTH_SHORT).show()
                        //finish() // 가입창 종료
                    } else {
                        Toast.makeText(this, "계정 생성 실패",Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}