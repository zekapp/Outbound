package com.outbound.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by zeki on 17/10/2014.
 */
@ParseClassName("NoticeBoard")
public class PNoticeBoard extends ParseObject {
    public String objectId = "objectId";
    public String createdBy = "createdBy";
    public String noticeboardTitle = "noticeBoardTitle";
    public String participants = "participants";
    public String country = "country";
    public String city = "city";
    public String travellerType = "travellerType";
    public String messages = "messages";
    public String noticeBoardLocation = "noticeBoardLocation";
    public String noticeBoardDate = "date";
    public String noticeBoardDescription = "description";
    public String noticeBoardPlace = "place";

    public String getObjectId() {
        return getString(objectId);
    }

    public PUser getCreatedBy() {
        return (PUser)get(createdBy);
    }

    public void setCreatedBy(PUser user) {
        put(createdBy, user);
    }

    public String getNoticeboardTitle() {
        return getString(noticeboardTitle);
    }

    public void setNoticeboardTitle(String title) {
        put(noticeboardTitle,title );
    }

    public List<PUser> getParticipants() {
        return  getList(participants);
    }

    public void setParticipants(PUser  user) {
        put(participants, user);
    }

    public String getCountry() {
        return getString(country);
    }

    public void setCountry(String cntr) {
        put(country, cntr);
    }

    public String getCity() {
        return getString(city);
    }

    public void setCity(String cty) {
        put(city, cty);
    }

    public List<String> getTravellerType() {
        return  getList(travellerType);
    }

    public void setTravellerType(List<String> list) {
        put(travellerType, list);
    }

    public JSONArray getMessages() {
        return getJSONArray(messages);
    }

    public void setMessages(JSONObject jsonMessage) {
        put(messages,jsonMessage);
    }

    public ParseGeoPoint getNoticeBoardLocation() {
        return getParseGeoPoint(noticeBoardLocation);
    }

    public void setNoticeBoardLocation(String location) {
        put(noticeBoardLocation,location);
    }

    public Date getNoticeBoardDate() {
        return getDate(noticeBoardDate);
    }

    public void setNoticeBoardDate(Date date) {
        put(noticeBoardDate,date );
    }

    public String getDescription() {
        return getString(noticeBoardDescription);
    }

    public void setDescription(String description) {
        put(noticeBoardDescription,description );
    }

    public String getPlace() {
        return  getString(noticeBoardPlace);
    }

    public void setNoticeBoardPlace(String place) {
        put(noticeBoardPlace, place);
    }

    public static void findNoticeBoardPostsWhitinKm(float v) {
//        ParseQuery<PNoticeBoard> query =
    }
}
