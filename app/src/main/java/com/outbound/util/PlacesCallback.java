package com.outbound.util;

import com.outbound.model.Place;
import com.outbound.model.WifiSpot;

import java.util.List;

/**
 * Created by zeki on 29/10/2014.
 */
public abstract class  PlacesCallback<WifiSpot> {
    public abstract void  done(List<WifiSpot> wifiSpots, Exception e);
}
