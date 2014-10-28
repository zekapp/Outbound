package com.outbound.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 27/10/2014.
 */
@ParseClassName("WifiSpot")
public class PWifiSpot extends ParseObject {
    private static final String TAG = makeLogTag(PWifiSpot.class);

    public final static String wifiName = "name";
    public final static String wifiAddress = "address";
    public final static String wifiLocation = "location";
    public final static String wifiType = "wifiType";
    public final static String isUserCreated = "userCreated";

    public  String getWifiName() {
        return getString(wifiName);
    }

    public void setWifiName(String name){
        put(wifiName, name);
    }

    public  String getWifiAddress() {
        return getString(wifiAddress);
    }
    public void setWifiAddress(String addr){
        put(wifiAddress,addr);
    }

    public ParseGeoPoint getWifiLocation() {
        return getParseGeoPoint(wifiLocation);
    }
    public void setWifiLocation(ParseGeoPoint location){
        put(wifiLocation,location);
    }

    public  String getWifiType() {
        return getString(wifiType);
    }
    public void setWifiType(String type){
        put(wifiType,type);
    }


    public void setIsUserCreated(boolean b){
        put(isUserCreated,b);
    }
    public  boolean getIsUserCreated() {
        return getBoolean(isUserCreated);
    }

    public static void findPurchaseTypeWifi(ParseGeoPoint location,final FindCallback<PWifiSpot> callback) {
        ParseQuery<PWifiSpot> query = ParseQuery.getQuery(PWifiSpot.class);
        query.whereEqualTo(wifiType, "withPurchase");
        if(location != null)
            query.whereWithinKilometers(wifiLocation,location,10); // search 10 km
        else
            query.whereWithinKilometers(wifiLocation,PUser.getCurrentUser().getCurrentLocation(),10); //search 10 km
        query.findInBackground(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                callback.done(pWifiSpots,e);
            }
        });
    }

    public static void findPaidTypeWifi(ParseGeoPoint location,final FindCallback<PWifiSpot> callback) {
        ParseQuery<PWifiSpot> query = ParseQuery.getQuery(PWifiSpot.class);
        query.whereEqualTo(wifiType, "paid");
        if(location != null)
            query.whereWithinKilometers(wifiLocation,location,10); // search 10 km
        else
            query.whereWithinKilometers(wifiLocation,PUser.getCurrentUser().getCurrentLocation(),10); //search 10 km
        query.findInBackground(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                callback.done(pWifiSpots,e);
            }
        });
    }

    public static void findFreeTypeWifi(ParseGeoPoint location,final FindCallback<PWifiSpot> callback) {
        ParseQuery<PWifiSpot> query = ParseQuery.getQuery(PWifiSpot.class);
        query.whereEqualTo(wifiType, "free");
        if(location != null)
            query.whereWithinKilometers(wifiLocation,location,10); // search 10 km
        else
            query.whereWithinKilometers(wifiLocation,PUser.getCurrentUser().getCurrentLocation(),10); //search 10 km
        query.findInBackground(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                callback.done(pWifiSpots,e);
            }
        });
    }

    public static void findAllTypeWifi(ParseGeoPoint location, final FindCallback<PWifiSpot> callback) {
        ParseQuery<PWifiSpot> query = ParseQuery.getQuery(PWifiSpot.class);
        if(location != null)
            query.whereWithinKilometers(wifiLocation,location,10); // search 10 km
        else
            query.whereWithinKilometers(wifiLocation,PUser.getCurrentUser().getCurrentLocation(),10); //search 10 km
//        query.orderByAscending(wifiLocation);
        query.findInBackground(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                callback.done(pWifiSpots,e);
            }
        });
    }
}
