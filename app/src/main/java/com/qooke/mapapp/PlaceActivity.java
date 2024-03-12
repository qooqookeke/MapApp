package com.qooke.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qooke.mapapp.model.Place;

import java.util.ArrayList;

public class PlaceActivity extends AppCompatActivity {

    ArrayList<Place> placeArrayList;

    // 내 위치 적용하기 위한 멤버변수
    double myLat;
    double myLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // 메인 액티비티에서 보낸 데이터 받아오기(메인 액티비티에서 내 위치를 계속 갱신하니까 해당 정보 가져오기)
        placeArrayList = (ArrayList<Place>) getIntent().getSerializableExtra("placeArrayList");
        myLat = getIntent().getDoubleExtra("myLat", 0);
        myLng = getIntent().getDoubleExtra("myLng", 0);


        // 맵을 나오게 하는 함수
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 1. 나의 위치를 지도의 중심에 놓고
                LatLng myLocation = new LatLng(myLat, myLng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));

                // 2. 상점들의 위치를 마커로 표시한다.
                for( Place place : placeArrayList) { // 반복문 반복하면서 place 변수에 정보를 저장함
                    LatLng latLng = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                    MarkerOptions options = new MarkerOptions();
                    googleMap.addMarker(options.position(latLng).title(place.name));
                }
            }
        });
    }
}