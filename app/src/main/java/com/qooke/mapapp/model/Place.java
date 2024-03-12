package com.qooke.mapapp.model;

import java.io.Serializable;

// 모든 클래스 직렬화(Serializable)
public class Place implements Serializable {

    public String name;
    public String vicinity;


    // 클래스화해서 result 데이터 가져오기(json 트리형으로 확인하면 편함)
    public Geometry geometry;
    public class Geometry implements Serializable {
        public Location location;
        public class Location implements Serializable {
            public double lat;
            public double lng;
        }
    }


    // 디폴트 생성자
    public Place() {

    }


    // 생성자

}
