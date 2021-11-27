package com.example.safe_return_home_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class setting: AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    lateinit var btn_logout : Button
    var count = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        auth = Firebase.auth

        var shake = arrayOf("1","2","3","4","5")
        btn_logout=findViewById(R.id.btn_logout)
        // 로그아웃
        var spinner = findViewById<Spinner>(R.id.fshake)
        var adapter: ArrayAdapter<String>
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, shake)
        spinner.adapter = adapter

        spinner.onItemSelectedListener
        spinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //count=(shake[p2]).toInt()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
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