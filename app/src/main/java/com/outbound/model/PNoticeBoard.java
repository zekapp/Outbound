package com.outbound.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.outbound.util.GenericMessage;
import com.outbound.util.MessagesResultCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.outbound.util.JsonUtils.convertClassToJsonObject;
import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 17/10/2014.
 */
@ParseClassName("NoticeBoard")
public class PNoticeBoard extends ParseObject {

    private static final String TAG = makeLogTag(PNoticeBoard.class);

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
//        List<PUser> participantsArray = getParticipants();
//
//        if(participantsArray == null)
//        {
//            participantsArray = new ArrayList<PUser>();
//            participantsArray.add(user);
//        }
//        else
//        {
//            participantsArray.add(user);
//        }
//
//        put(participants, participantsArray);
        add(participants,user);
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

    public void setTravellerType(String[] list) {
        put(travellerType, Arrays.asList(list));
    }

    public List<GenericMessage> getAllMessages() {

        List<GenericMessage> messageList = new ArrayList<GenericMessage>();

        List<HashMap<String, Object>> hashMaps = getList(messages);

        if(hashMaps != null){
            for(HashMap<String, Object> hashMap : hashMaps){
                GenericMessage message = new GenericMessage();
                message.generateFromHasMap(hashMap);
                messageList.add(message);
            }
            return messageList;
        }else
            return null;




////        Gson gson = new Gson();
////        String jsonString = getString(messages);
//        JSONArray array = getJSONArray(messages);
//        if(array == null)
//            return  null;
//
//        String str = array.toString();
////        String str = getTempString();
//
//        List<NoticeBoardMessage> boardMessageList =
//                gson.fromJson(str, new TypeToken<ArrayList<NoticeBoardMessage>>(){}.getType());
//
////        reGenerateTheParseFile(boardMessageList, array);
//        if(boardMessageList!=null)
//            return boardMessageList;
//        else
//            return null;
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
                LOGD(TAG, "reGenerateTheParseFile e:" + e.getMessage());
            }
            i++;


        }
    }

//    private ParseFile parseArrayForParseFile(JSONArray array) {
//        String tag_hotel_id =  "profilePicture";
//        ParseFile parseFile = new ParseFile("sda",);
//    }

    public void setMessages(GenericMessage message) throws JSONException {
//        List<JSONObject> res = JsonUtils.convertClassToJsonString(getAllMessages(), message);
//        addIfThisUserIsNew(participants, PUser.getCurrentUser());
//        put(messages, res);

        addIfThisUserIsNew(PUser.getCurrentUser());
//        add(messages, convertClassToJsonObject(message));
        put(messages, message);
    }

    private void addIfThisUserIsNew(PUser currentUser) {
        List<PUser> party = getParticipants();
        boolean isInParty = false;
        if(party != null){
            for(PUser u : party){
                if(u.getObjectId().equals(currentUser.getObjectId()))
                {
                    isInParty = true;
                    break;
                }
            }
        }else
            isInParty= false;


        if(!isInParty)
            this.setParticipants(currentUser);
    }

    public GenericMessage getMessageItem(int item){
        List<GenericMessage> boardMessageList = getAllMessages();
        if(boardMessageList != null && boardMessageList.size() >= item)
            return boardMessageList.get(item);
        else
            return null;
    }

    public int getMessagesCount(){
        List<GenericMessage> boardMessageList = getAllMessages();
        if(boardMessageList != null)
            return boardMessageList.size();
        else
            return 0;
    }
    public int getParticipiantCount(){
        List<PUser> part = getParticipants();
        if(part != null)
            return part.size();
        else
            return 0;
    }


    public ParseGeoPoint getNoticeBoardLocation() {
        return getParseGeoPoint(noticeBoardLocation);
    }

    public void setNoticeBoardLocation(ParseGeoPoint location) {
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

    public void setPlace(String plc){
        put(noticeBoardPlace,plc);
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


    public static void findPostsWhitSearcRequest(ParseQuery<PNoticeBoard> query, final FindCallback<PNoticeBoard> callback) {
        query.include(PNoticeBoard.createdBy);
        query.findInBackground(new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                callback.done(pNoticeBoards,e);
            }
        });
    }


    public static void fetchNewMessages(final int adapterCurrentSize, PNoticeBoard post,final MessagesResultCallback<GenericMessage> callback) {
        ParseQuery<PNoticeBoard> query = ParseQuery.getQuery(PNoticeBoard.class);
        query.whereEqualTo(PNoticeBoard.objectId, post.getObjectId());

        query.getFirstInBackground(new GetCallback<PNoticeBoard>() {
            @Override
            public void done(PNoticeBoard pNoticeBoard, ParseException e) {
                if(e == null){
                    List<GenericMessage> messageList = getLastMessages(pNoticeBoard.getAllMessages(), adapterCurrentSize);
                    callback.done(messageList,e);
                }else
                    callback.done(null,e);

            }
        });
    }

    private static List<GenericMessage> getLastMessages(List<GenericMessage> allMessages, int adapterCurrentSize) {
        List<GenericMessage> res = new ArrayList<GenericMessage>();
        PUser currentUser = PUser.getCurrentUser();

        if(allMessages == null)
            return res;

        for (int i= adapterCurrentSize ; i < allMessages.size(); i++){
            GenericMessage msg = allMessages.get(i);

            if(!msg.getUserID().equals(currentUser.getObjectId())){
                res.add(msg);
            }
        }
        return res;
    }
}
