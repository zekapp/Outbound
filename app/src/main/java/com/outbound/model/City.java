package com.outbound.model;

import com.parse.ParseGeoPoint;

/**
 * Created by zeki on 9/10/2014.
 */
public class City {
    private String cityName;
    private ParseGeoPoint geoPoint;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public ParseGeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(ParseGeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
