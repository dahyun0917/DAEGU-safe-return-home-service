package com.example.safe_return_home_service


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.*
import android.media.MediaRecorder
import android.os.*
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*

class monitoring : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
    private var LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private val mapView: MapView by lazy { findViewById(R.id.map_view) }
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var geocoder:Geocoder
    lateinit var btn_mike: ImageButton
    lateinit var btn_setting: ImageButton
    lateinit var btn_signal: ImageButton
    lateinit var btn_cctv: ImageButton
    lateinit var btn_store: ImageButton
    lateinit var btn_police: ImageButton
    lateinit var btn_moni: ImageButton

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    var time = 0
    var count= 0
    private var timerTask: Timer? = null

    var police=0
    var policeArray:ArrayList<Marker> = ArrayList()
    var storeArray:ArrayList<Marker> = ArrayList()
    var cctvArray:ArrayList<Marker> = ArrayList()
    var store=0
    var cctv=0
    val infoWindow = InfoWindow()

    /*private val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_SMS
    )*/

    var APIKEY_ID = "j01tozred3"
    var APIKEY = "qMNcbT8wDWV2X56NmQCHYIsFxeNWPvXvmZUznXHo"

    var apiURL ="https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start={35.890150, 128.611087}&goal={35.882775, 128.612691}"

    //var apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start={35.890150, 128.611087}&goal={35.882775, 128.612691}"

    private val currentLocationButton: LocationButtonView by lazy { findViewById(R.id.currentLocationButton) }
    //센서 관리자 객체 얻기
    private val sensorManager1 by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    var eventTime = 0

    // 중력, 중력가속도을 기준으로 삼아서 진동, 움직임을 측정한다.
    // 흔들림 감지할 때 기준이 되는 가해지는 힘
    private var SHAKE_THRESHOLD_GRAVITY = 2.7F

    // 흔들림 감지할때 최소 0.5초를 기준으로 측정한다.
    private var SHAKE_SKIP_MS = 500

    // 흔드는 횟수는 3초마다 초기화
    private var SHAKE_COUNT_RESET_TIME_MS = 3000
    //private lateinit var db:FirebaseFirestore
    var now_lat=0.0;var now_long=0.0

    var REQUEST_CODE_LOCATION = 2

    private val locationManager1 by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    private var mShakeCount = 0
    var fbFirestore: FirebaseFirestore? = null
    var locationManager: LocationManager ?=null
    //private val multiplePermissionsCode = 100
    //var rejectedPermissionList = ArrayList<String>()

    lateinit var sms : SmsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monitoring)

        var db = FirebaseFirestore.getInstance() //firebase
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //geocoder
        geocoder=Geocoder(this)

        btn_cctv = findViewById<ImageButton>(R.id.btn_cctv)
        btn_store = findViewById<ImageButton>(R.id.btn_store)
        btn_police = findViewById<ImageButton>(R.id.btn_police)
        btn_mike = findViewById<ImageButton>(R.id.mike)
        btn_setting = findViewById<ImageButton>(R.id.setting)
        btn_signal = findViewById<ImageButton>(R.id.signal)
        btn_moni = findViewById(R.id.walk)
        var btn_load = findViewById<Button>(R.id.load)
        var edit_start = findViewById<EditText>(R.id.start)
        var edit_arrive = findViewById<EditText>(R.id.arrive)
        var btn_exit = findViewById<Button>(R.id.exit)
        fbFirestore = FirebaseFirestore.getInstance()
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)



        btn_setting.setOnClickListener {
            val intent = Intent(this, setting::class.java)
            startActivity(intent)
        }
        btn_mike.setOnClickListener {
            val intent = Intent(this, record_list::class.java)
            startActivity(intent)

        }
        btn_signal.setOnClickListener {
           // var userLocation = getMyLocation()!!


            SendSMS()

        }
//        btn_moni.setOnClickListener {
//            val intent = Intent(this, monitoring::class.java)
//            startActivity(intent)
//        }

        btn_police.setOnClickListener {
            if (police == 0) {
                val jsonString = assets.open("jsons/emergency.json").reader().readText()

                var jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val marker = Marker()
                    var jo = jsonArray.getJSONObject(i)
                    var local = jo.getString("지역")
                    var name = jo.getString("name")
                    var location = jo.getString("location")
                    var latitude = jo.getDouble("latitude")
                    var longitude = jo.getDouble("longitude")
                    marker.icon = OverlayImage.fromResource(R.drawable.police_station)
                    marker.width = 130
                    marker.height = 130
                    marker.tag = "$name\n위치 : $location"
                    marker.setOnClickListener {
                        infoWindow.open(marker)
                        true
                    }
                    marker.position = LatLng(latitude, longitude)
                    policeArray.add(marker)
                    marker.map = naverMap
                }

                police = 1
            } else {
                for (marker in policeArray) {
                    marker.map = null
                }
                policeArray.clear()
                police = 0
            }

        }
        btn_store.setOnClickListener {
            if (store == 0) {
                val jsonString = assets.open("jsons/convenience.json").reader().readText()

                var jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val marker = Marker()
                    var jo = jsonArray.getJSONObject(i)
                    var name = jo.getString("편의점 이름")
                    var location = jo.getString("위치")
                    var latitude = jo.getDouble("latitude")
                    var longitude = jo.getDouble("longitude")
                    marker.icon = OverlayImage.fromResource(R.drawable.store_pin2)
                    marker.width = 130
                    marker.height = 130
                    marker.tag = "$name\n위치 : $location"
                    marker.setOnClickListener {
                        infoWindow.open(marker)
                        true
                    }
                    marker.position = LatLng(latitude, longitude)
                    storeArray.add(marker)
                    marker.map = naverMap
                }
                store = 1
            } else {
                for (marker in storeArray) {
                    marker.map = null
                }
                storeArray.clear()
                store = 0
            }

        }
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        btn_cctv.setOnClickListener {
            if (cctv == 0) {
                val jsonString = assets.open("jsons/cctv.json").reader().readText()

                var jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length()) {
                    val marker = Marker()
                    var jo = jsonArray.getJSONObject(i)
                    var local = jo.getString("지역")
                    var latitude = jo.getDouble("latitude")
                    var longitude = jo.getDouble("longitude")
                    marker.icon = OverlayImage.fromResource(R.drawable.cctv_pin)
                    marker.width = 40
                    marker.height = 40
                    marker.position = LatLng(latitude, longitude)
                    cctvArray.add(marker)
                    marker.map = naverMap
                }
                cctv = 1
            } else {
                for (marker in cctvArray) {
                    marker.map = null
                }
                cctvArray.clear()
                cctv = 0
            }
        }

        btn_load.setOnClickListener {

            var startlocation = edit_start.text.toString();
            var arrivelocation = edit_arrive.text.toString();
            var list : List<Address>?=null

            //Toast.makeText(this, "${now_lat}, ${now_long}", Toast.LENGTH_SHORT).show()


            if (arrivelocation==null||arrivelocation=="")  {
                Toast.makeText(this,"도착지를 입력해주세요!",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


//            var current=getMyLocation()!!

            Log.d("location","위도 : ${now_lat}, 경도 : ${now_long}")
            val retrofit =
                Retrofit.Builder().baseUrl("https://naveropenapi.apigw.ntruss.com/map-direction/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(NaverAPI::class.java)

            var start_long=0.0; var start_lat=0.0
            var goal_long=0.0; var goal_lat=0.0

            try{
                list=geocoder.getFromLocationName(arrivelocation,1)
                var mlat = list?.get(0)?.latitude
                var mlng = list?.get(0)?.longitude
                if (mlat != null&&mlng!=null) {
                    goal_lat=mlat.toDouble()
                    goal_long=mlng.toDouble()
                }
            }
            catch(e:IOException){
                e.printStackTrace()
                Toast.makeText(this@monitoring, "목적지 주소를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (startlocation==""||startlocation==null) {
                if(now_long!=0.0&&now_lat!=0.0){
                    start_long=now_long
                    start_lat=now_lat
                }
                else{
                    Toast.makeText(this@monitoring, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            else{
                try{
                    list=geocoder.getFromLocationName(startlocation,1)
                    var mlat = list?.get(0)?.latitude
                    var mlng = list?.get(0)?.longitude
                    if (mlat != null&&mlng!=null) {
                        start_lat=mlat.toDouble()
                        start_long=mlng.toDouble()
                    }
                }
                catch(e:IOException){
                    e.printStackTrace()
                    Toast.makeText(this@monitoring, "출발지 주소를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            //길찾기 시작

            startRecording()


            var callgetPath =
                api.getPath(APIKEY_ID, APIKEY, "${start_long}, ${start_lat}", "${goal_long}, ${goal_lat}")


            btn_load.visibility= View.INVISIBLE
            edit_start.visibility=View.INVISIBLE
            edit_arrive.visibility=View.INVISIBLE
            btn_exit.visibility=View.VISIBLE

            callgetPath.enqueue(object : Callback<ResultPath> {
                override fun onResponse(
                    call: Call<ResultPath>,
                    response: Response<ResultPath>
                ) {
                    var path_cords_list = response.body()?.route?.traoptimal
                    //경로 그리기 응답바디가 List<List<Double>> 이라서 2중 for문 썼음
                    val path = PathOverlay()
                    //MutableList에 add 기능 쓰기 위해 더미 원소 하나 넣어둠
                    val path_container: MutableList<LatLng>? = mutableListOf(LatLng(0.1, 0.1))
                    for (path_cords in path_cords_list!!) {
                        for (path_cords_xy in path_cords?.path) {
                            //구한 경로를 하나씩 path_container에 추가해줌
                            path_container?.add(LatLng(path_cords_xy[1], path_cords_xy[0]))
                        }
                    }
                    //더미원소 드랍후 path.coords에 path들을 넣어줌.
                    path.coords = path_container?.drop(1)!!
                    path.color = Color.RED
                    path.map = naverMap

                    //경로 시작점으로 화면 이동
                    if (path.coords != null) {
                        val cameraUpdate = CameraUpdate.scrollTo(path.coords[0]!!)
                            .animate(CameraAnimation.Fly, 3000)
                        naverMap!!.moveCamera(cameraUpdate)

                        Toast.makeText(this@monitoring, "경로 안내가 시작됩니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultPath>, t: Throwable) {
                    TODO("Not yet implemented")
                }


            })
            SendSMS() //message 전송
        }

        btn_exit.setOnClickListener {
            timerTask?.cancel();
            count=1
            stopRecording()
            val i = Intent(this, monitoring::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }
        private fun getMyLocation(): Location? {
            var currentLocation: Location? = null

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            var locationProvider: String = LocationManager.GPS_PROVIDER
            currentLocation = locationManager?.getLastKnownLocation(locationProvider)
            if (currentLocation != null) {
                var lng = currentLocation.longitude
                var lat = currentLocation.latitude
            }
            return currentLocation

        }

        override fun onMapReady(naverMap: NaverMap) {
            //맵 가져오기(from: getMapAsync)
            this.naverMap = naverMap
            //줌 범위 설정
            naverMap.maxZoom = 18.0
            naverMap.minZoom = 10.0
            //지도 위치 이동
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.8874092, 128.6127373))
            naverMap.moveCamera(cameraUpdate)
            //현위치 버튼 기능
            val uiSetting = naverMap.uiSettings
            uiSetting.isLocationButtonEnabled = false

            currentLocationButton.map = naverMap
            locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
            naverMap.locationSource = locationSource

            naverMap.setOnMapClickListener { pointF, latLng ->
                infoWindow.close()
            }

            naverMap.addOnLocationChangeListener { location ->
                now_lat = location.latitude;now_long = location.longitude;

                //Toast.makeText(this, "${location.latitude}, ${location.longitude}",
                //    Toast.LENGTH_SHORT).show()
            }

            //위험도 표시
            val jsonString = assets.open("jsons/danger.json").reader().readText()

            var jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val marker = Marker()
                var jo = jsonArray.getJSONObject(i)
                var dong = jo.getString("dong")
                var latitude = jo.getDouble("latitude")
                var longitude = jo.getDouble("longtitude")
                var score = jo.getDouble("score")

                if (score >= 2.5) {
                    marker.icon = OverlayImage.fromResource(R.drawable.danger4)
                } else if (score >= 2) {
                    marker.icon = OverlayImage.fromResource(R.drawable.danger3)
                } else if (score >= 1.5) {
                    marker.icon = OverlayImage.fromResource(R.drawable.danger2)
                } else {
                    marker.icon = OverlayImage.fromResource(R.drawable.danger1)
                }

                marker.width = 30
                marker.height = 30
                marker.position = LatLng(latitude, longitude)
                //cctvArray.add(marker)
                marker.map = naverMap
            }


        }

        override fun onStart() {
            super.onStart()
            mapView?.onStart()
        }

        override fun onResume() {
            super.onResume()
            mapView?.onResume()
            sensorManager1.registerListener(
                this,
                sensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        override fun onPause() {
            super.onPause()
            mapView?.onPause()
            sensorManager1.unregisterListener(this)
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            mapView?.onSaveInstanceState(outState)
        }

        override fun onStop() {
            super.onStop()
            mapView?.onStop()
        }

        override fun onDestroy() {
            super.onDestroy()
            mapView?.onDestroy()
        }

        override fun onLowMemory() {
            super.onLowMemory()
            mapView?.onLowMemory()
        }

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    var axisX: Float = event.values[0]
                    var axisY: Float = event.values[1]
                    var axisZ: Float = event.values[2]

                    var gravityX: Float = axisX / SensorManager.GRAVITY_EARTH
                    var gravityY: Float = axisY / SensorManager.GRAVITY_EARTH
                    var gravityZ: Float = axisZ / SensorManager.GRAVITY_EARTH

                    var f: Float = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ
                    var squaredD: Double = Math.sqrt(f.toDouble())
                    var gForce: Float = squaredD.toFloat()

                    if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                        var currentTime: Long = System.currentTimeMillis()
                        if (SystemClock.elapsedRealtime() - eventTime < SHAKE_SKIP_MS) {
                            return
                        }
                        eventTime = currentTime.toInt()
                        mShakeCount++
                        Log.d(TAG, "Shake 발생 " + mShakeCount)
                        Toast.makeText(this@monitoring, "Shake 발생", Toast.LENGTH_SHORT).show()

                        /*for (permission in requiredPermissions) {
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    permission
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                //만약 권한이 없다면 rejectedPermissionList에 추가
                                rejectedPermissionList.add(permission)
                            }
                        }
                        if (rejectedPermissionList.isNotEmpty()) {
                            //권한 요청!
                            val array = arrayOfNulls<String>(rejectedPermissionList.size)
                            ActivityCompat.requestPermissions(
                                this,
                                rejectedPermissionList.toArray(array),
                                multiplePermissionsCode
                            )
                            SendSMS()
                        } else {
                            SendSMS()
                        }*/
                        SendSMS()
                        //현재 위치 디비에 저장
                        // 메세지 112랑 보호자에게 현재위치랑 같이 메세지
                    }

                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        fun SendSMS() {
            //var phoneNo = "01025335441";
            //var sms = "안녕";
            var latitude: Double = 0.0
            var longitude: Double = 0.0
            var userLocation = getMyLocation()!!
            if (userLocation != null) {
                latitude = userLocation.latitude
                longitude = userLocation.longitude
                Toast.makeText(this@monitoring, "$latitude $longitude", Toast.LENGTH_LONG).show()

            }
            var lat: String = latitude.toString()
            var lon: String = longitude.toString()
            var LatLon = location_data()
            LatLon.lat = lat
            LatLon.lng = lon
            fbFirestore?.collection("reported info")?.document()?.set(LatLon)
            Toast.makeText(this@monitoring, "현재위치가 신고되었습니다..", Toast.LENGTH_SHORT).show()
            sms = SmsManager.getDefault()
            sms.sendTextMessage(
                "01090489628",
                null,
                "$lat $lon",
                null,
                null
            )
            Toast.makeText(this@monitoring, "보호자에게 문자발송이 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    private fun startRecording(){
        //config and create MediaRecorder Object
        var file= File(Environment.getExternalStorageDirectory().path+"/Download/"+"safe_return_home/")
        if(!file.exists()){
            file.mkdirs()
        }
        val fileName: String = Date().getTime().toString() + ".mp3"
        output = Environment.getExternalStorageDirectory().absolutePath + "/Download/"+"safe_return_home/" + fileName//내장메모리 밑에 위치
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this@monitoring, "레코딩 시작되었습니다.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@monitoring, "레코딩 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)
        }
    }
    fun timer(){
        time=0
        timerTask = kotlin.concurrent.timer(period = 1000,initialDelay = 1000) { //반복주기는 peroid 프로퍼티로 설정, 단위는 1000분의 1초 (period = 1000, 1초)
            time++ // period=10으로 0.01초마다 time를 1씩 증가하게 됩니다
            if (time == 7200) {
                timerTask?.cancel();
                //Toast.makeText(this@signal, "시간초과로 중지 되었습니다.", Toast.LENGTH_SHORT).show()
                stopRecording()
            }
        }
    }
}
