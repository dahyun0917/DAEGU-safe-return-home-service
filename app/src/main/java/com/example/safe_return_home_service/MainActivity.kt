package com.example.safe_return_home_service

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.fragment.app.FragmentManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    private var LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private val mapView: MapView by lazy{findViewById(R.id.map_view)}
    private lateinit var locationSource : FusedLocationSource
    private lateinit var naverMap : NaverMap
   // private val locationManager= context
    //    .getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    private val currentLocationButton: LocationButtonView by lazy { findViewById(R.id.currentLocationButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //네이버 지도
        //mapView = findViewById<View>(R.id.map_view) as MapView

        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)

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