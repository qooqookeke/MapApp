package com.qooke.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qooke.mapapp.adapter.PlaceAdapter;
import com.qooke.mapapp.api.NetworkClient;
import com.qooke.mapapp.api.PlaceApi;
import com.qooke.mapapp.config.Config;
import com.qooke.mapapp.model.Place;
import com.qooke.mapapp.model.PlaceList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    EditText editKeyword;
    Button imgSearch;
    ProgressBar progressBar;


    // 나의 GPS 정보 가져오기 위한 멤버 변수
    LocationManager locationManager;
    LocationListener locationListener;

    // 현재 나의 위치값을 저장할 멤버 변수
    double lat;
    double lng;
    boolean isLocationReady;


    // 필요에 의해 멤버변수로 만듬
    final int radius = 2000; // 반경 2km
    final String language = "ko";
    String keyword = "";


    // 페이징 처리를 위한 변수
    String pagetoken = "";


    // 리사이클러뷰 관련 멤버 변수
    RecyclerView recyclerView;
    PlaceAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editKeyword = findViewById(R.id.editKeyword);
        imgSearch = findViewById(R.id.imgSearch);
        progressBar = findViewById(R.id.progressBar);

        recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 고정사이즈:true, 변동사이즈:false
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // 리사이클러뷰 페이징 처리
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 맨 마지막 데이터가 화면에 나타나면 네트워크 통해서 데이터를 추가로 받아오고 화면에 표시한다.
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalcount = recyclerView.getAdapter().getItemCount();

                // 스크롤을 맨 끝까지 한 상태 체크
                if(lastPosition+1 == totalcount) {
                    if (pagetoken.isEmpty() == false) {
                        // 네트워크 통해서 데이터를 추가로 받아오고 화면에 표시한다.
                        addNetworkData();
                    }

                }
            }
        });


        // 위치를 가져오기 위해서는 시스템 서비스로부터 로케이션 매니저를 받아온다.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 위도
                lat = location.getLatitude();
                // 경도
                lng = location.getLongitude();

                isLocationReady = true;
            }
        };


        // gps 권한 허용하는 코드
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        // 3000 = 3초 / 거리 이동시 -1, 내 위치값 3초마다 불러오는 함수
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, -1, locationListener);




        // 검색 버튼 누르면 실행되는 함수
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = editKeyword.getText().toString().trim();

                if (keyword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isLocationReady == false) {
                    Toast.makeText(MainActivity.this, "아직 내 위치를 못 찾았습니다. 잠시후에 이용하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                getNetworkData();

            }
        });


    }

    private void addNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        // api호출
        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceList(lat +","+lng,
                                                    radius, language, keyword, pagetoken, Config.API_KEY);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    PlaceList placeList = response.body();
                    if (placeList.next_page_token != null) {
                        pagetoken = placeList.next_page_token;
                    } else {
                        pagetoken = "";
                    }

                    placeArrayList.addAll(placeList.results);

                    adapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void getNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        // api 호출
        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);
        Call<PlaceList> call = api.getPlaceList(lat + "," + lng,
                                                radius, language, keyword, Config.API_KEY);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                Log.i("AAA", ""+response.code());

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    PlaceList placeList = response.body();

                    // 넥스트 페이지 토큰 가져와서 멤버변수 pagetoken에 저장
                    if(placeList.next_page_token != null) {
                        pagetoken = placeList.next_page_token;
                    }

                    placeArrayList.clear();
                    placeArrayList.addAll(placeList.results);

                    // 어댑터 생성
                    adapter = new PlaceAdapter(MainActivity.this, placeArrayList);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    // 위치권한 허용 확인 하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // 허용하지 않았을때 다시 허용하라는 알러트 띄운다.
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                finish();
                return;
            }
            // 허용했으면 GPS 정보 가져오는 코드 넣는다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, -1, locationListener);
        }
    }

    // 액션바에 아이콘 나오게 하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 액션바 아이콘 눌렀을때 처리할 행동(액티비티 이동하기)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menuMap){
            // 새로운 액티비티에 데이터 보내면서 열기
            Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
            intent.putExtra("placeArrayList", placeArrayList);
            // 내 위치 정보도 PlaceActivity로 보내기
            intent.putExtra("myLat", lat);
            intent.putExtra("myLng", lng);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}