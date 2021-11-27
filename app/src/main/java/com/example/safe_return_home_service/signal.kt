package com.example.safe_return_home_service

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.io.IOException
import java.lang.reflect.Array.get
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.HashMap


class signal: AppCompatActivity(){
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false

    lateinit var btn_cancle : Button
    var clientId = "ooywnneloz"
    var clientSecret = "4CXBAfjeIwW4pAxpecj9lC1v5r5D0vF3UJcSj8Hk"

    var time = 0

    var count=0
    private var timerTask: Timer? = null

    lateinit var sms : SmsManager
    //현재위치 가져오기 위함
    lateinit var locationManager : LocationManager
    var REQUEST_CODE_LOCATION = 2

    //위도,경도 바꾸기 위해
    //val geocoder = Geocoder(this)

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
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

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

        btn_cancle.setOnClickListener {
            timerTask?.cancel();
            count=1
            stopRecording()
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)
        }
//        requestHeaders.put("X-NCP-APIGW-API-KEY-ID:",clientId)
 //       requestHeaders.put("X-NCP-APIGW-API-KEY:",clientSecret)


    }

    private fun getMyLocation(): Location? {
        var currentLocation : Location? = null

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        var locationProvider:String = LocationManager.GPS_PROVIDER
        currentLocation = locationManager.getLastKnownLocation(locationProvider)
        if(currentLocation != null){
            var lng = currentLocation.longitude
            var lat = currentLocation.latitude
        }
        return currentLocation
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
        var latitude : Double = 0.0
        var longitude : Double = 0.0
        var userLocation = getMyLocation()!!
        var list : List<Address>?=null
        if(userLocation != null){
            latitude = userLocation.latitude
            longitude = userLocation.longitude
            Toast.makeText(this@signal, "$latitude $longitude", Toast.LENGTH_LONG).show()
//            var apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$latitude ,$longitude"
//            try{
//                list= geocoder.getFromLocation(
//                    latitude!!,
//                    longitude!!,
//                    1
//                )
//            }catch (e : IOException){
//                e.printStackTrace()
//                Toast.makeText(this@signal, "주소를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
//            }
//
//            if(list != null){
//                if(list.size !=0)
//                Log.d("현재 주소",list[0].getAddressLine(0))
//                else {
//                    Toast.makeText(this@signal, "해당되는 주소 정보는 없습니다.", Toast.LENGTH_SHORT).show()
//
//                }
            //}
        }

        sms = SmsManager.getDefault()
        sms.sendTextMessage(
            "01025335441",
            null,
            "hi",
            null,
            null
        )
        Toast.makeText(this@signal, "문자발송.", Toast.LENGTH_SHORT).show()


    }
}