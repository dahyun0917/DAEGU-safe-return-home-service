package com.example.safe_return_home_service

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*
import java.util.jar.Manifest


class signal: AppCompatActivity() {
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false

    lateinit var btn_cancle : Button

    var time = 0

    var count=0
    private var timerTask: Timer? = null


    lateinit var sms : SmsManager

    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signal)
        btn_cancle = findViewById(R.id.btn_cancle)
        /*val permissions=ArrayList<String>()
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) +
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS))
            != PackageManager.PERMISSION_GRANTED) {

            permissions.add(android.Manifest.permission.READ_SMS)
            permissions.add(android.Manifest.permission.SEND_SMS)
            /*val permissions = arrayOf(android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS)
            ActivityCompat.requestPermissions(this, permissions,0)*/
        }
        else if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.RECORD_AUDIO)
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            //Permission is not granted
            /*val permissionss = arrayOf(android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissionss,0)*/
        }
        if(permissions.isNotEmpty()){
            val array = arrayOfNulls<String>(permissions.size)
            ActivityCompat.requestPermissions(this, permissions.toArray(array), multiplePermissionsCode)
        }*/

        var rejectedPermissionList = ArrayList<String>()
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
            SendSMS()
            startRecording()
        }
        else{
            SendSMS()
            startRecording()
        }



        /*if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
            startRecording()
        } else {
            startRecording()
        }*/
        btn_cancle.setOnClickListener {
            timerTask?.cancel();
            count=1
            stopRecording()
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)
        }



    }


    private fun startRecording(){
        //config and create MediaRecorder Object
        val fileName: String = Date().getTime().toString() + ".mp3"
        output = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this@signal, "레코딩 시작되었습니다.", Toast.LENGTH_SHORT).show()
            timer()
            //if(state==false)Toast.makeText(this@signal, "시간초과로 중지 되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        }

    }
    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
            if(count==1) Toast.makeText(getApplicationContext(),"중지 되었습니다.", Toast.LENGTH_LONG).show();
            else{
            Looper.prepare();
            Toast.makeText(getApplicationContext(),"시간초과로 중지 되었습니다.", Toast.LENGTH_LONG).show();
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)
            Looper.loop();
            }
        } else {
            Toast.makeText(this@signal, "레코딩 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)
        }
    }
    fun timer(){
        time=0
        timerTask = kotlin.concurrent.timer(period = 1000,initialDelay = 1000) { //반복주기는 peroid 프로퍼티로 설정, 단위는 1000분의 1초 (period = 1000, 1초)
            time++ // period=10으로 0.01초마다 time를 1씩 증가하게 됩니다
            if (time == 6) {
                timerTask?.cancel();
                //Toast.makeText(this@signal, "시간초과로 중지 되었습니다.", Toast.LENGTH_SHORT).show()
                stopRecording()
            }
        }
    }
    fun SendSMS(){
        //var phoneNo = "01025335441";
        //var sms = "안녕";

        sms = SmsManager.getDefault()
        sms.sendTextMessage("01025335441", null, "안녕", null,null)
        Toast.makeText(this@signal, "문자발송.", Toast.LENGTH_SHORT).show()


    }




}