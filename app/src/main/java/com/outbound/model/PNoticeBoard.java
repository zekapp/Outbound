package com.outbound.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zeki on 17/10/2014.
 */
@ParseClassName("NoticeBoard")
public class PNoticeBoard extends ParseObject {
    public final static String objectId = "objectId";
    public final static String createdBy = "createdBy";
    public final static String noticeboardTitle = "noticeBoardTitle";
    public final static String participants = "participants";
    public final static String country = "country";
    public final static String city = "city";
    public final static String travellerType = "travellerType";
    public final static String messages = "messages";
    public final static String noticeBoardLocation = "noticeBoardLocation";
    public final static String noticeBoardDate = "date";
    public final static String noticeBoardDescription = "description";
    public final static String noticeBoardPlace = "place";

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

    public List<NoticeBoardMessage> getAllMessages() {
        Gson gson = new Gson();
//        String jsonString = getString(messages);
        JSONArray array = getJSONArray(messages);
        if(array == null)
            return  null;

        String str = array.toString();
//        String str = getTempString();

        List<NoticeBoardMessage> boardMessageList =
                gson.fromJson(str, new TypeToken<ArrayList<NoticeBoardMessage>>(){}.getType());

//        reGenerateTheParseFile(boardMessageList, array);
        if(boardMessageList!=null)
            return boardMessageList;
        else
            return null;
    }

    private String getTempString() {
        return "[\n" +
                "  {\n" +
                "    \"date\": \"18/10/14\",\n" +
                "    \"profilePicture\": {\n" +
                "      \"__type\": \"File\",\n" +
                "      \"name\": \"tfss-89054040-4102-4094-a1f3-d78c192f23fc-file\",\n" +
                "      \"url\": \"http://files.parsetfss.com/a6cbff63-5287-446f-ad39-23c771a2b099/tfss-89054040-4102-4094-a1f3-d78c192f23fc-file\"\n" +
                "    },\n" +
                "    \"text\": \"Yes\",\n" +
                "    \"userID\": \"Mxx5twXYIk\",\n" +
                "    \"userName\": \"Ryan Hanly\"\n" +
                "  }\n" +
                "]";
    }

    private void reGenerateTheParseFile(List<NoticeBoardMessage> boardMessageList, JSONArray array) {
        int i =0 ;
        for(NoticeBoardMessage msg : boardMessageList){
            try {
                JSONObject obj = array.getJSONObject(i);
                Object fetchedObjt = obj.get("profilePicture");
                msg.setProfilePicture((ParseFile)fetchedObjt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;


        }
    }

//    private ParseFile parseArrayForParseFile(JSONArray array) {
//        String tag_hotel_id =  "profilePicture";
//        ParseFile parseFile = new ParseFile("sda",);
//    }

    public void setMessages(NoticeBoardMessage msg) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(msg);
        put(messages,jsonString);
    }

    public NoticeBoardMessage getMessageItem(int item){
        List<NoticeBoardMessage> boardMessageList = getAllMessages();
        if(boardMessageList != null)
            return boardMessageList.get(item);
        else
            return null;
    }

    public int getMessagesCount(){
        List<NoticeBoardMessage> boardMessageList = getAllMessages();
        if(boardMessageList != null)
            return boardMessageList.size();
        else
            return 0;
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

    public static void findPostsWhitinKm(float dist, final FindCallback<PNoticeBoard> callback) {
        ParseQuery<PNoticeBoard> query = ParseQuery.getQuery(PNoticeBoard.class);
        query.whereWithinKilometers(PNoticeBoard.noticeBoardLocation,
                PUser.getCurrentUser().getCurrentLocation(),dist);
        query.orderByDescending(PNoticeBoard.createdBy);
        query.include(PNoticeBoard.createdBy);
//        query.whereNotContainedIn(PNoticeBoard.createdBy, PUser.getCurrentUser().getBlockedBy());
        query.findInBackground(new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                callback.done(pNoticeBoards,e);
            }
        });
    }

    public static void findPostsWhitinCountry(final FindCallback<PNoticeBoard> callback) {
        ParseQuery<PNoticeBoard> query = ParseQuery.getQuery(PNoticeBoard.class);
        query.whereEqualTo(PNoticeBoard.country, PUser.getCurrentUser().getCurrentCountry());
        query.orderByDescending(PNoticeBoard.createdBy);
        query.include(PNoticeBoard.createdBy);
//        query.whereNotContainedIn(PNoticeBoard.createdBy, PUser.getCurrentUser().getBlockedBy());
        query.findInBackground(new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                callback.done(pNoticeBoards,e);
            }
        });
    }
    public static void findPostsWhitinWorld(final FindCallback<PNoticeBoard> callback) {
        ParseQuery<PNoticeBoard> query = ParseQuery.getQuery(PNoticeBoard.class);
        query.orderByDescending(PNoticeBoard.createdBy);
        query.include(PNoticeBoard.createdBy);
//        query.whereNotContainedIn(PNoticeBoard.createdBy, PUser.getCurrentUser().getBlockedBy());
        query.findInBackground(new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                callback.done(pNoticeBoards,e);
            }
        });
    }
}
