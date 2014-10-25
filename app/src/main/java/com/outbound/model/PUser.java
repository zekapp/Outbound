package com.outbound.model;

import com.outbound.util.ResultCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zeki on 25/08/2014.
 */

@ParseClassName("_User")
public class PUser extends ParseUser {

    public static final String strCurrentLocation = "currentLocation";
    public static final String strObjectId = "objectId";
    public static final String facebookId = "facebookID";
    public static final String userName = "username";
    public static final String gender = "gender";
    public static final String emailStr = "email";
    public static final String profileImage = "profileImage";
    public static final String backGroundIamge = "backGroundImage";
    public static final String homeTown = "homeTown";
    public static final String sexualPreference = "sexualPreference";
    public static final String travelType = "travelType";
    public static final String currentLocation = "currentLocation";
    public static final String shortDescription = "shortDescription";
    public static final String nextTravels = "NextTravels";
    public static final String age = "age";
    public static final String friends = "friends";
    public static final String countryCode = "countryCode";
    public static final String viewedBy = "viewedBy";
    public static final String currentCity = "currentCity";
    public static final String currentCountry = "currentCountry";
    public static final String nationality = "nationality";
    public static final String blockedBy = "blockedBy";


    public static PUser getCurrentUser(){
        return (PUser)ParseUser.getCurrentUser();
    }

    // Facebook Id
    public String getFacebookID() {
        return getString("facebookID");
    }
    public void setFacebookID(String id){
        put("facebookID",id);
    }

    //User name
    public String getUserName() {
        return getString("username");
    }
    public void setUserName(String userName){
        put("username", userName);
    }

    //Gender
    public String getGender(){
        return getString("gender");
    }
    public void setGender(String gender){
        put("gender", gender);
    }

    //Email
    public String getEmail(){
        return getString("email");
    }
    public void setEmail(String email){
        put("email", email);
    }

    //ProfilePicture
    public ParseFile getProfilePicture(){
        return getParseFile("profileImage");
    }
    public void setProfilePicture(ParseFile file){
        put("profileImage", file);
    }

    //CoverPicture
    public ParseFile getCoverPicture(){
        return getParseFile("backGroundImage");
    }
    public void setCoverPicture(ParseFile file){
        put("backGroundImage", file);
    }

    //Nationality
    public String getNationality(){
        return getString("nationality");
    }
    public void setNationality(String nationality){
        put("nationality", nationality);
    }

    //Hometown
    public String getHometown(){
        return getString("homeTown");
    }
    public void setHometown(String hometown){
        put("homeTown", hometown);
    }

    //SexualPref
    public String getSexualPref(){
        return getString("sexualPreference");
    }
    public void setSexualPref(String[] sex){
        put("sexualPreference", Arrays.asList(sex));
    }

    //TravelerType
    public List<String> getTravelerType(){
        return getList("travelType");
    }
    public void setTravelerType(String[] type){
        put("travelType", Arrays.asList(type));
    }

    //CurrentLocation
    public ParseGeoPoint getCurrentLocation(){
        return getParseGeoPoint("currentLocation");
    }
    public void setCurrentLocation(ParseGeoPoint curLoc){
        put("currentLocation", curLoc);
    }

    //ShortDescription
    public String getShortDescription(){
        return getString("shortDescription");
    }
    public void setShortDescription(String description){
        put("shortDescription", description);
    }

    //NextTravel
    public List<String> getNextTravel(){
        return getList("NextTravels");
    }
    public void setNextTravel(String[] nextTravel){
        put("NextTravels", nextTravel);
    }

    //DateOfBirth
    public Date getDateOfBirth(){
        return getDate("age");
    }
    public void setDateOfBirth(Date dateOfBirth){
        put("age", dateOfBirth);
    }

    //Friends
    public List<PUser> getFriends(){
        return getList("friends");
    }
    public void setFriends(PUser[] friends){
        put("friends", friends);
    }

    //CountryCode
    public String getCountryCode(){
        return getString("countryCode");
    }
    public void setCountryCode(String cntrCode){
        put("countryCode", cntrCode);
    }


    //ViewedBy
    public String getViewedBy(){
        return getString("viewedBy");
    }
    public void setViewedBy(String viewedBy){
        put("viewedBy", viewedBy);
    }


    //CurrentCity
    public String getCurrentCity(){
        return getString("currentCity");
    }
    public void setCurrentCity(String currentCity){
        put("currentCity", currentCity);
    }


    //CurrentCountry
    public String getCurrentCountry(){
        return getString("currentCountry");
    }
    public void setCurrentCountry(String currentCountry){
        put("currentCountry", currentCountry);
    }

    //BlockedBy
    public List<PUser> getBlockedBy(){
        return getList(blockedBy);
    }
    public void setBlockedByUser(PUser user){
        //use cloud code
    }


    public int getAge() {
        Date curDate = new Date();
        if(getDateOfBirth() != null)
            return  (int)(TimeUnit.MILLISECONDS.toDays(curDate.getTime() - getDateOfBirth().getTime()) / 365);
        else
            return -1;
    }

    public String calculateDistanceinKmTo(PUser user){
        ParseGeoPoint peopleCurrentLoc = this.getCurrentLocation();

        if(peopleCurrentLoc == null)
            peopleCurrentLoc = new ParseGeoPoint(0,0);

        double distance = peopleCurrentLoc.distanceInKilometersTo(user.getCurrentLocation());
        DecimalFormat formatter = new DecimalFormat("##.#");
        return formatter.format(distance)+"km";
    }

    public static void getUserListAsDistanceOrder(List<PUser> userList, final FindCallback<PUser> callback){


//        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
//        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
    }

    public static void getPeopleArroundMe(final FindCallback<PUser> callback){
        PUser currentUser = PUser.getCurrentUser();
        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
        query.findInBackground(new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pUsers, ParseException e) {
                callback.done(pUsers,e);
            }
        });
    }

    public static void blockThisUser(PUser user , final ResultCallback callback) {
        ParseCloud.callFunctionInBackground("block_user",new AbstractMap<String, Object>() {
            @Override
            public Set<Entry<String, Object>> entrySet() {
                return null;
            }
        }, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if(e == null){
                    callback.done(true, null);
                }else
                    callback.done(false, e);
            }
        });
    }

    public static void fetchTheSpesificUserFromId(String userObjectID, final GetCallback<PUser> callback) {
        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
        query.whereEqualTo(PUser.strObjectId, userObjectID);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getFirstInBackground(new GetCallback<PUser>() {
            @Override
            public void done(PUser pUser, ParseException e) {
                callback.done(pUser, e);
            }
        });
    }
}
