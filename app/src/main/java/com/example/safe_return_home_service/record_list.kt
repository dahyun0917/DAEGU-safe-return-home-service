package com.example.safe_return_home_service

import android.content.Context
import android.content.pm.PackageManager
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

class record_list : AppCompatActivity() {

    //lateinit var mp3List: ArrayList<String>
    lateinit var selectedMP3: String
    lateinit var pbMP3: ProgressBar
    lateinit var recordTime : TextView
    //var recordListView: ListView? = null

    var mp3Path=Environment.getExternalStorageDirectory().path+"/Download/"
    lateinit var mPlayer: MediaPlayer
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record)


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //만약 권한이 없다면 rejectedPermissionList에 추가
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        mp3List = ArrayList()
        var listFiles = File(mp3Path).listFiles()
        var fileName: String
        var extName: String
        for (file in listFiles!!) {
            fileName = file.name
            extName = fileName.substring(fileName.length - 3)
            if (extName == "mp3")
            // 확장명이 mp3일 때만 추가함.
                mp3List.add(fileName)
        }

        var rclist = findViewById<ListView>(R.id.rclist)
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mp3List)
        //rclist.choiceMode = ListView.CHOICE_MODE_SINGLE
        rclist.adapter = adapter
        rclist.setItemChecked(0, true)

        rclist.setOnItemClickListener{ arg0, arg1, arg2, arg3 ->
            selectedMP3 = mp3List[arg2]

            mPlayer = MediaPlayer()
            mPlayer.setDataSource(mp3Path + selectedMP3)
            mPlayer.prepare()
            mPlayer.start()


        }
    }*/
    var mp3List= ArrayList<record>()
    val items = mutableListOf<record>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //만약 권한이 없다면 rejectedPermissionList에 추가
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        //mp3List = ArrayList()
        var listFiles = File(mp3Path).listFiles()
        var fileName: String
        var extName: String
        //pbMP3 = findViewById<ProgressBar>(R.id.pbMP3)
        //recordTime=findViewById(R.id.recordTime)

        for (file in listFiles!!) {
            //fileTime=file.t
            fileName = file.name
            extName = fileName.substring(fileName.length - 3)
            if (extName == "mp3") {
                // 확장명이 mp3일 때만 추가함.
                //mp3List.add(fileName)
                mp3List.add(record("$fileName","00:05:10","play",1))
            }
        }

        //rlist.add(record("녹음파일1","00:05:10","play"))
        //rlist.add(record("녹음파일1","00:05:10","play"))
        var recordListView = findViewById<ListView>(R.id.recordListView)/*여기서 오류*/

        val recordAdapter = MainListAdapter(this, mp3List)
        recordListView.adapter = recordAdapter
        //recordListView.adapter = MainListAdapter(this, rlist)



        recordListView.setOnItemClickListener{ parent: AdapterView<*>, view: View, position: Int, id: Long ->

            Toast.makeText(this, "hi",Toast.LENGTH_SHORT).show()
            val item = parent.getItemAtPosition(position) as record

            //selectedMP3 = mp3List[arg2]
            selectedMP3= item.toString()
            mPlayer = MediaPlayer()
            mPlayer.setDataSource(mp3Path + selectedMP3)
            mPlayer.prepare()
            mPlayer.start()

            /*var a =0
            object : Thread() {
                var timeFormat = SimpleDateFormat("mm:ss")
                override fun run() {
                    if (mPlayer == null)
                        return
                    pbMP3.max = mPlayer.duration
                    a=mPlayer.duration
                    while (mPlayer.isPlaying) {
                        runOnUiThread {
                            pbMP3.progress = mPlayer.currentPosition
                            recordTime.text = "진행 시간 : " + timeFormat.format(mPlayer.currentPosition)
                        }
                        SystemClock.sleep(200)
                    }
                }
            }.start()

            Toast.makeText(this, "$a",Toast.LENGTH_SHORT).show()*/
        }
    }



}