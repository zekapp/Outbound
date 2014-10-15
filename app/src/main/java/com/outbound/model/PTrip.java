package com.outbound.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zeki on 14/10/2014.
 */
@ParseClassName("Trip")
public class PTrip extends ParseObject{
    public static final String strCity = "city";
    public static final String strCountry  = "country";
    public static final String strCountryCode    = "countryCode";
    public static final String strFromDate    = "fromDate";
    public static final String strToDate    = "toDate";
    public static final String strOutBounder    = "outBounder";

    public String getCity(){
        return getString(strCity);
    }
    public void setCity(String city){
        put(strCity,city);
    }

    public String getCountry(){
        return getString(strCountry);
    }
    public void setCountry(String country){
        put(strCountry,country);
    }

    public String getCountryCode(){
        return getString(strCountryCode);
    }
    public void setCountryCode(String countryCode){
        put(strCountryCode,countryCode);
    }

    public Date getFromDate(){
        return getDate(strFromDate);
    }
    public void setFromDate(Date date){
        put(strFromDate, date);
    }

    public Date getToDate(){
        return getDate(strToDate);
    }
    public void setToDate(Date date){
        put(strToDate,date);
    }

    public PUser getOutBounder(){
        return (PUser)get(strOutBounder);
    }
    public void setOutBounder(PUser user){
        put(strOutBounder,user);
    }

    public static void findUsersAttendingThisTrip(PTrip trip, final FindCallback<PUser> callback){

        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
        query.whereEqualTo(strCity, trip.getCity());
        query.whereEqualTo(strCountry, trip.getCountry());
        query.whereGreaterThanOrEqualTo(strFromDate, trip.getFromDate());
        query.whereLessThanOrEqualTo(strToDate,trip.getToDate());
        query.include(strOutBounder);

        query.findInBackground(new FindCallback<PTrip>() {
            @Override
            public void done(List<PTrip> pTrips, ParseException e) {
                List<PUser> pUsers = new ArrayList<PUser>();
                for(PTrip pTrip : pTrips){
                    pUsers.add(pTrip.getOutBounder());
                }
                callback.done(pUsers,e);
            }
        });

//        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
//        query.whereEqualTo(strCity, trip.getCity());
//        query.whereEqualTo(strCountry, trip.getCountry());
//
//        ParseQuery<PUser> userParseQuery = ParseQuery.getQuery(PUser.class);
//        userParseQuery.whereMatchesKeyInQuery(PUser.strObjectId, PTrip.strOutBounder, query);
//        userParseQuery.findInBackground(new FindCallback<PUser>() {
//            @Override
//            public void done(List<PUser> pUsers, ParseException e) {
//                callback.done(pUsers,e);
//            }
//        });

    }

    public static void findUserSpecificFutureTrips(PUser user,final FindCallback<PTrip> callback ){
        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
        query.whereEqualTo(strOutBounder, user);
        query.whereGreaterThanOrEqualTo(strFromDate, new Date());
        query.findInBackground(new FindCallback<PTrip>() {
            @Override
            public void done(List<PTrip> pTrips, ParseException e) {
                callback.done(pTrips,e);
            }
        });
    }

    public static void findUserTripInSpecificTripDateInterval(PTrip trip, PUser user, final GetCallback<PTrip> callback) {
        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
        query.whereEqualTo(strOutBounder, user);
        query.whereGreaterThanOrEqualTo(strFromDate, trip.getFromDate());
        query.whereLessThanOrEqualTo(strToDate, trip.getToDate());
        query.getFirstInBackground(new GetCallback<PTrip>() {
            @Override
            public void done(PTrip pTrip, ParseException e) {
                callback.done(pTrip,e);
            }
        });
    }

    public static void addTrip(String city, String country, Date fromDate, Date toDate, final SaveCallback callback) {
        PTrip trip = new PTrip();
        trip.setCity(city);
        trip.setCountry(country);
        trip.setFromDate(fromDate);
        trip.setToDate(toDate);
        trip.setOutBounder(PUser.getCurrentUser());
        trip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(e);
            }
        });
    }

    public static void bringTripsOrderedDate(final FindCallback<PTrip> callback) {
        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
        query.orderByAscending(strFromDate);
        query.findInBackground(new FindCallback<PTrip>() {
            @Override
            public void done(List<PTrip> pTrips, ParseException e) {
                callback.done(pTrips,e);
            }
        });
    }

    public static void findUserSpecificHistoryTrips(PUser user, final FindCallback<PTrip> callback) {
        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
        query.whereEqualTo(strOutBounder, user);
        query.whereGreaterThanOrEqualTo(strFromDate, new Date());
        query.findInBackground(new FindCallback<PTrip>() {
            @Override
            public void done(List<PTrip> pTrips, ParseException e) {
                callback.done(pTrips,e);
            }
        });
    }
}
