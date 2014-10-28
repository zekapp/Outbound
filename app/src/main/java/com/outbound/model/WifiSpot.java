package com.outbound.model;

import android.location.Location;

import com.parse.ParseGeoPoint;

/**
 * Created by zeki on 28/10/2014.
 */
public class WifiSpot {

    private String wifiName;
    private String wifiAddress;
    private ParseGeoPoint wifiLocation;
    private String wifiType;

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiAddress() {
        return wifiAddress;
    }

    public void setWifiAddress(String wifiAddress) {
        this.wifiAddress = wifiAddress;
    }

    public ParseGeoPoint getWifiLocation() {
        return wifiLocation;
    }

    public void setWifiLocation(ParseGeoPoint wifiLocation) {
        this.wifiLocation = wifiLocation;
    }

    public String getWifiType() {
        return wifiType;
    }

    public void setWifiType(String wifiType) {
        this.wifiType = wifiType;
    }
}
