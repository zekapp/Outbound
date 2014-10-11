package com.outbound.util;

/**
 * Created by zeki on 11/10/2014.
 */
public abstract class FindAddressCallback<T> {

    public Runnable action;

    protected FindAddressCallback(Runnable action){
        this.action = action;
    }
    public abstract void done(String countryName, String cityName, String countryCode, Exception e);
}
