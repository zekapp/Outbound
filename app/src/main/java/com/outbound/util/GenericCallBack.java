package com.outbound.util;

/**
 * Created by zeki on 23/10/2014.
 */
public abstract class  GenericCallBack<T> {
    public abstract void  done(T res, com.parse.ParseException e);
}
