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

public class MapActivity extends AppCompatActivity {

    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 메인 액티비티에서 보낸 데이터 받아오기
        place = (Place) getIntent().getSerializableExtra("place");


        // 맵을 나오게 하는 함수
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 1. 지도의 중심을 가게좌표로 이동하고
                double lat = place.geometry.location.lat;
                double lng = place.geometry.location.lng;

                LatLng latLng = new LatLng(lat, lng);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                // 2. 마커도 표시한다.
                MarkerOptions options = new MarkerOptions();
                googleMap.addMarker(options.position(latLng).title(place.name));

            }
        });

    }
}