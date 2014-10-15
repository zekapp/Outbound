package com.outbound.util;

import android.location.Location;

import com.parse.ParseGeoPoint;

/**
 * Created by zeki on 15/10/2014.
 */
public abstract class GeoCodeCallback{
    public abstract void done( ParseGeoPoint location, Exception e);
}
