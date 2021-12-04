package com.example.safe_return_home_service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class record_list : AppCompatActivity() {

    //lateinit var mp3List: ArrayList<String>
    lateinit var hap : String
    var current: String? = null
    //var recordListView: ListView? = null
    var setplay=0
    var mp3Path: String? =null
    lateinit var mPlayer: MediaPlayer

    var mp3List= ArrayList<record>()
    var mp3name=ArrayList<String>()
    var mp3time=ArrayList<String>()

    var count=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //만약 권한이 없다면 rejectedPermissionList에 추가
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
        var file= File(Environment.getExternalStorageDirectory().path+"/Download/"+"safe_return_home/")
        if(!file.exists()){
            file.mkdirs()
        }
        mp3Path=Environment.getExternalStorageDirectory().path+"/Download/"+"safe_return_home/"

        //mp3List = ArrayList()
        var listFiles = File(mp3Path).listFiles()
        var fileName: String
        var extName: String

        for (file in listFiles!!) {
            //fileTime=file.t
            fileName = file.name
            extName = fileName.substring(fileName.length - 3)

            if (extName == "mp3") {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(mp3Path+fileName)
                val time=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInmillisec=java.lang.Long.parseLong(time)
                hap=getTime(timeInmillisec)
                // 확장명이 mp3일 때만 추가함.
                mp3name.add(fileName)
                mp3time.add(hap)
                //mp3List.add(fileName)
                mp3List.add(record("$fileName","$hap","play"))
            }
        }

        var recordListView = findViewById<ListView>(R.id.recordListView)/*여기서 오류*/

        val recordAdapter = MainListAdapter(this, mp3List)
        recordListView.adapter = recordAdapter
        //recordListView.adapter = MainListAdapter(this, rlist)

        var btn_setting = findViewById<ImageButton>(R.id.setting)

        btn_setting.setOnClickListener {
            val intent = Intent(this, setting::class.java)
            startActivity(intent)
        }

        //recordListView.setOnItemClickListener{ parent: AdapterView<*>, view: View, position: Int, id: Long ->
        recordListView.setOnItemClickListener{ arg1,arg2,arg3,arg4 ->
            //Toast.makeText(this, "hi",Toast.LENGTH_SHORT).show()

            var fname=mp3name[arg3]
            var ftime=mp3time[arg3]


            /*if(setplay==0) {
                mPlayer = MediaPlayer()
                mPlayer.setDataSource(mp3Path + fname)
                mPlayer.prepare()
                mPlayer.start()
                mp3List.set(arg3, record("$fname", "$ftime", "stop"))
                setplay=1
            }
            else{
                mPlayer.stop()
                mp3List.set(arg3, record("$fname", "$ftime", "play"))
                setplay=0
            }
            val recordAdapter = MainListAdapter(this, mp3List)
            recordListView.adapter = recordAdapter
            */
            if((current.equals(fname))||count==0){
                count=1
                if(setplay==0) {
                    mPlayer = MediaPlayer()
                    mPlayer.setDataSource(mp3Path + fname)
                    mPlayer.prepare()
                    mPlayer.start()
                    mp3List.set(arg3, record("$fname", "$ftime", "stop"))
                    setplay=1
                }
                else{
                    mPlayer.stop()
                    mp3List.set(arg3, record("$fname", "$ftime", "play"))
                    setplay=0
                    count=0
                }
                val recordAdapter = MainListAdapter(this, mp3List)
                recordListView.adapter = recordAdapter
                current=fname
            }
            else
                Toast.makeText(this, "하나의 녹음 파일만 실행할 수 있습니다.",Toast.LENGTH_SHORT).show()

        }




    }
    fun getTime(ftime:Long):String{

        var seconds = ((ftime/ 1000) % 60).toInt() //초
        var minutes = ((ftime/ (1000 * 60) % 60)).toInt() //분
        var hours = ((ftime / (1000 * 60 * 60) % 24)).toInt() //시
        //"$hours" + ":" + "$minutes" + ":" + "$seconds"
        var hours1 : String
        var minutes1 : String
        var seconds1 : String
        if(hours/10==0) {
            hours1 = "0"+"$hours"
        }
        else{
            hours1="$hours"
        }

        if(minutes/10==0){
            minutes1="0"+"$minutes"
        }
        else{
            minutes1="$minutes"
        }
        if(seconds/10==0){
            seconds1="0"+"$seconds"
        }
        else{
            seconds1="$seconds"
        }
        val hap="$hours1" + ":" + "$minutes1" + ":" + "$seconds1"

        return hap
    }





}