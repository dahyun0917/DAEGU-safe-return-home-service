package com.example.safe_return_home_service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import org.json.JSONArray


class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    private var LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private val mapView: MapView by lazy{findViewById(R.id.map_view)}
    private lateinit var locationSource : FusedLocationSource
    private lateinit var naverMap : NaverMap
    lateinit var btn_mike : ImageButton
    lateinit var btn_setting : ImageButton
    lateinit var btn_signal : ImageButton
    lateinit var btn_cctv : ImageButton
    lateinit var btn_store : ImageButton
    lateinit var btn_police : ImageButton
    lateinit var btn_moni : ImageButton
    var police=0
    var policeArray:ArrayList<Marker> = ArrayList()
    var storeArray:ArrayList<Marker> = ArrayList()
    var cctvArray:ArrayList<Marker> = ArrayList()
    var store=0
    var cctv=0
    val infoWindow = InfoWindow()
    var now_lat=0.0;var now_long=0.0


    // private val locationManager= context
    //    .getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    private val currentLocationButton: LocationButtonView by lazy { findViewById(R.id.currentLocationButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //firestore


        //네이버 지도
        //mapView = findViewById<View>(R.id.map_view) as MapView
        btn_cctv = findViewById<ImageButton>(R.id.btn_cctv)
        btn_store = findViewById<ImageButton>(R.id.btn_store)
        btn_police = findViewById<ImageButton>(R.id.btn_police)
        btn_mike = findViewById<ImageButton>(R.id.mike)
        btn_setting = findViewById<ImageButton>(R.id.setting)
        btn_signal = findViewById<ImageButton>(R.id.signal)
        btn_moni = findViewById(R.id.walk)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)



        btn_setting.setOnClickListener{
            val intent = Intent(this,setting ::class.java)
            startActivity(intent)
        }
        btn_mike.setOnClickListener{
            val intent = Intent(this,record_list ::class.java)
            startActivity(intent)
        }
        btn_signal.setOnClickListener{
            val intent = Intent(this,signal ::class.java)
            intent.putExtra("now_lat",now_lat)
            intent.putExtra("now_long",now_long)
            startActivity(intent)
        }
        btn_moni.setOnClickListener {
            val intent = Intent(this,monitoring ::class.java)
            startActivity(intent)
        }

        btn_police.setOnClickListener {
            if(police==0){
                val jsonString=assets.open("jsons/emergency.json").reader().readText()

                var jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length() ){
                    val marker= Marker()
                    var jo = jsonArray.getJSONObject(i)
                    var local=jo.getString("지역")
                    var name=jo.getString("name")
                    var location=jo.getString("location")
                    var latitude=jo.getDouble("latitude")
                    var longitude=jo.getDouble("longitude")
                    marker.icon= OverlayImage.fromResource(R.drawable.police_station)
                    marker.width=130
                    marker.height=130
                    marker.tag="$name\n위치 : $location"
                    marker.setOnClickListener {
                        infoWindow.open(marker)
                        true
                    }
                    marker.position=LatLng(latitude,longitude)
                    policeArray.add(marker)
                    marker.map=naverMap
                }

                police=1
            }
            else{
                for(marker in policeArray){
                    marker.map=null
                }
                policeArray.clear()
                police=0
            }

        }
        btn_store.setOnClickListener {
            if(store==0){
                val jsonString=assets.open("jsons/convenience.json").reader().readText()

                var jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length() ){
                    val marker= Marker()
                    var jo = jsonArray.getJSONObject(i)
                    var name=jo.getString("편의점 이름")
                    var location=jo.getString("위치")
                    var latitude=jo.getDouble("latitude")
                    var longitude=jo.getDouble("longitude")
                    marker.icon= OverlayImage.fromResource(R.drawable.store_pin2)
                    marker.width=130
                    marker.height=130
                    marker.tag="$name\n위치 : $location"
                    marker.setOnClickListener {
                        infoWindow.open(marker)
                        true
                    }
                    marker.position=LatLng(latitude,longitude)
                    storeArray.add(marker)
                    marker.map=naverMap
                }
                store=1
            }
            else{
                for(marker in storeArray){
                    marker.map=null
                }
                storeArray.clear()
                store=0
            }

        }
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        btn_cctv.setOnClickListener {
            if(cctv==0){
                val jsonString=assets.open("jsons/cctv.json").reader().readText()

                var jsonArray = JSONArray(jsonString)

                for (i in 0 until jsonArray.length() ){
                    val marker= Marker()
                    var jo = jsonArray.getJSONObject(i)
                    var local=jo.getString("지역")
                    var latitude=jo.getDouble("latitude")
                    var longitude=jo.getDouble("longitude")
                    marker.icon= OverlayImage.fromResource(R.drawable.cctv_pin)
                    marker.width=40
                    marker.height=40
                    marker.position=LatLng(latitude,longitude)
                    cctvArray.add(marker)
                    marker.map=naverMap
                }
                cctv=1
            }
            else{
                for(marker in cctvArray){
                    marker.map=null
                }
                cctvArray.clear()
                cctv=0
            }
        }
//        locationSource = FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE)
//        naverMap.locationSource = locationSource
//        var fragmentManager : FragmentManager = supportFragmentManager;
//        var mapFragment : MapFragment = fragmentManager.findFragmentById(R.id.map_view) as MapFragment
//        if(mapFragment == null){
//            mapFragment = MapFragment.newInstance()
//            fragmentManager.beginTransaction().add(R.id.map_view,mapFragment).commit()
//        }
//        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE )
                return
        if(locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)){
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        //맵 가져오기(from: getMapAsync)
        this.naverMap = naverMap
        //줌 범위 설정
        naverMap.maxZoom =18.0
        naverMap.minZoom = 10.0
        //지도 위치 이동
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497801,127.027591))
        naverMap.moveCamera(cameraUpdate)
        //현위치 버튼 기능
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false

        currentLocationButton.map = naverMap
        locationSource= FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE)
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
        val jsonString=assets.open("jsons/danger.json").reader().readText()

        var jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length() ){
            val marker= Marker()
            var jo = jsonArray.getJSONObject(i)
            var dong=jo.getString("dong")
            var latitude=jo.getDouble("latitude")
            var longitude=jo.getDouble("longtitude")
            var score=jo.getDouble("score")

            if(score>=2.5){
                marker.icon= OverlayImage.fromResource(R.drawable.danger4)
            }
            else if(score>=2){
                marker.icon= OverlayImage.fromResource(R.drawable.danger3)
            }
            else if(score>=1.5){
                marker.icon= OverlayImage.fromResource(R.drawable.danger2)
            }
            else{
                marker.icon= OverlayImage.fromResource(R.drawable.danger1)
            }

            marker.width=30
            marker.height=30
            marker.position=LatLng(latitude,longitude)
            //cctvArray.add(marker)
            marker.map=naverMap
        }

    }

    override fun onStart(){
        super.onStart()
        mapView?.onStart()
    }
    override fun onResume(){
        super.onResume()
        mapView?.onResume()
    }
    override fun onPause(){
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }
    override fun onDestroy(){
        super.onDestroy()
        mapView?.onDestroy()
    }
    override fun onLowMemory(){
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
