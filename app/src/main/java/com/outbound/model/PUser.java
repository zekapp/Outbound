package com.outbound.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zeki on 25/08/2014.
 */

@ParseClassName("_User")
public class PUser extends ParseUser{

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
        return getString("hometown");
    }
    public void setHometown(String nationality){
        put("hometown", nationality);
    }

    //SexualPref
    public String getSexualPref(){
        return getString("sexualPreference");
    }
    public void setSexualPref(String sex){
        put("sexualPreference", sex);
    }

    //TravelerType
    public List<String> getTravelerType(){
        return getList("travelType");
    }
    public void setTravelerType(String[] sex){
        put("travelType", Arrays.asList(sex));
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
    public void setCountryCode(String countryCode){
        put("countryCode", countryCode);
    }

    //ViewedBy
    public String getViewedBy(){
        return getString("viewerBy");
    }
    public void setViewedBy(String viewedBy){
        put("viewerBy", viewedBy);
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

    public int getAge() {
        Date curDate = new Date();
        if(getDateOfBirth() != null)
            return  (int)(TimeUnit.MILLISECONDS.toDays(curDate.getTime() - getDateOfBirth().getTime()) / 365);
        else
            return -1;
    }
}
