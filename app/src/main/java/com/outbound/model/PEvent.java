package com.outbound.model;

import android.location.Location;

import com.outbound.util.ResultCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by zeki on 12/10/2014.
 */
@ParseClassName("Events")
public class PEvent extends ParseObject{
    public final static String objectId = "objectId";
    public final static String eventName = "eventName";
    public final static String description = "description";
    public final static String createdBy = "createdBy";
    public final static String outBoundersGoing = "outBoundersGoing";
    public final static String startDate = "startDate";
    public final static String startTime = "startTime";
    public final static String endDate = "endDate";
    public final static String endTime = "endTime";
    public final static String country = "country";
    public final static String city = "city";
    public final static String place = "place";
    public final static String eventLocationPoint = "eventLocationPoint";

    public Date getStartTime() {
        return getDate(startTime);
    }
    public void setStartTime(Date time) {
        put(startTime,time);
    }

    public Date getEndTime() {
        return getDate(endTime);
    }
    public void setEndTime(Date time) {
        put(endTime,time);
    }

    public String getEventName() {
        return getString(eventName);
    }

    public void setEventName(String name) {
        put(eventName, name);
    }

    public String getDescription() {
        return getString(description);
    }

    public void setDescription(String des) {
        put(description, des);
    }

    public PUser getCreatedBy() {
        return (PUser)get(createdBy);
    }

    public void setCreatedBy(PUser user) {
        put(createdBy, user);
    }

    public List<PUser> getOutboundersGoing() {
        return getList(outBoundersGoing);
    }

    public void setOutboundersGoing(List<PUser> going) {
        put(outBoundersGoing, going);
    }

    public Date getStartDate() {
        return getDate(startDate);
    }

    public void setStartDate(Date date) {
        put(startDate,date);
    }

    public Date getEndDate() {
        return getDate(endDate);
    }

    public void setEndDate(Date date) {
        put(endDate,date);
    }

    public String getCountry() {
        return getString(country);
    }

    public void setCountry(String cntry) {
        put(country, cntry);
    }

    public String getCity() {
        return getString(city);
    }

    public void setCity(String cty) {
        put(city, cty);
    }

    public String getPlace() {
        return getString(place);
    }

    public void setPlace(String plc) {
        put(place, plc);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(eventLocationPoint);
    }

    public void setLocation(ParseGeoPoint pLoc) {
        put(eventLocationPoint, pLoc);
    }

    public static void findEventsAroundCurrentUser
            (PUser currentUser, double proximity, final FindCallback<PEvent> callback) {

        ParseQuery<PEvent> query = ParseQuery.getQuery(PEvent.class);
        query.whereWithinRadians(eventLocationPoint,currentUser.getCurrentLocation(),proximity);
        query.orderByAscending(startDate);
//        query.include(PEvent.outBoundersGoing);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<PEvent>() {
            @Override
            public void done(List<PEvent> pEvents, ParseException e) {
                callback.done(pEvents,e);
            }
        });
    }

    public void fetchEventCreater(final GetCallback<PUser> callback){
        this.getCreatedBy().fetchIfNeededInBackground(new GetCallback<PUser>() {
            @Override
            public void done(PUser user, ParseException e) {
                callback.done(user,e);
            }
        });
    }

    public static void findEventsOfSpecificUser(PUser user, final FindCallback<PEvent> callback){
        ParseQuery<PEvent> query = ParseQuery.getQuery(PEvent.class);
        query.whereEqualTo(createdBy,user);
        query.orderByAscending(startDate);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<PEvent>() {
            @Override
            public void done(List<PEvent> pEvents, ParseException e) {
                callback.done(pEvents,e);
            }
        });
    }

    public static void joinTheEvent(PEvent event, final SaveCallback callback) {
        event.add(PEvent.outBoundersGoing, PUser.getCurrentUser());
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(e);
            }
        });
    }

    public static void checkIsAlreadyJoined(final PEvent event, final ResultCallback callback) {
        ParseQuery<PEvent> query = ParseQuery.getQuery(PEvent.class);
        query.whereEqualTo(PEvent.objectId, event.getObjectId());
        query.whereEqualTo(PEvent.outBoundersGoing, PUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<PEvent>() {
            @Override
            public void done(PEvent pEvent, ParseException e) {
                if(e != null){
                    if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        callback.done(false, null);
                    }else
                        callback.done(true,e);
                }else{
                    callback.done(true,e);
                }

            }
        });
    }
}
