package com.qooke.mapapp.api;

import com.qooke.mapapp.model.PlaceList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceApi {

    // 구글 nearbysearch api 호출
    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceList> getPlaceList(@Query("location") String location,
                                 @Query("radius") int radius,
                                 @Query("language") String language,
                                 @Query("keyword") String keyword,
                                 @Query("key") String apiKey);


    // 페이징 토큰 받아오기(자바에서는 함수 이름 똑같아도 파리미터가 다르면 괜찮음:메서드 오버로딩)
    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceList> getPlaceList(@Query("location") String location,
                                 @Query("radius") int radius,
                                 @Query("language") String language,
                                 @Query("keyword") String keyword,
                                 @Query("pagetoken") String pagetoken,
                                 @Query("key") String apiKey);

}
