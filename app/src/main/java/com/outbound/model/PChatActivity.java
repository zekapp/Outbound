package com.outbound.model;

import com.outbound.util.GenericCallBack;
import com.outbound.util.GenericMessage;
import com.outbound.util.JsonUtils;
import com.outbound.util.MessagesResultCallback;
import com.outbound.util.ResultCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;


import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 21/10/2014.
 */
@ParseClassName("ChatActivity")
public class PChatActivity extends ParseObject {
    private static final String TAG = makeLogTag(PChatActivity.class);

    public final static String objectId = "objectId";
    public final static String messages = "messages";
    public final static String participants = "participants";
    public final static String usersLeft = "usersLeft";
    public final static String chatType = "chatType";
    public final static String createdAt = "createdAt";
    public final static String updatedAt = "updatedAt";
    public final static String initiator = "initiator";

    private Date chatActivityFinalMessageDate;

//    public String getObjectId() {
//        return getString(objectId);
//    }

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
            return  null;
    }


    public GenericMessage getMessageItem(int item){
        List<GenericMessage> chatMessageList = getAllMessages();
        if(chatMessageList != null && chatMessageList.size() >= item)
            return chatMessageList.get(item);
        else
            return null;
    }

    public int getMessagesCount(){
        List<GenericMessage> chatMessageList = getAllMessages();
        if(chatMessageList != null)
            return chatMessageList.size();
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

    public void addMessage(GenericMessage msg){
        JSONObject msjObj = JsonUtils.convertClassToJsonObject(msg);
        add(messages, msjObj);
    }

    public List<PUser>  getParticipants() {
        return getList(participants);
    }

    public void addParticipant(PUser  user) {
        add(participants,user);
    }

    private void putParticipants(List<PUser> users) {
        put(participants, users);
    }

    public void addUserLeft(PUser  user){
        add(usersLeft, user);
    }
    public List<PUser>  getUserleft() {
        return getList(usersLeft);
    }

    public void setChatType(String type){
        put(chatType, type);
    }
    public String getChatType() {
        return getString(chatType);
    }

    public Date getCreatedAt() {
        return getDate(createdAt);
    }

    public PUser getInitiator() {
        return (PUser)get(initiator);
    }

    private static List<GenericMessage> getLastMessagesOfEachThread(List<PChatActivity> pChatActivities) {
        List<GenericMessage> res = new ArrayList<GenericMessage>();

        for (PChatActivity pChatActivity : pChatActivities){
            int messageCount = pChatActivity.getMessagesCount();
            if(messageCount > 0)
                res.add(pChatActivity.getMessageItem(messageCount-1)); // last message;
        }
        return res;
    }

//    public Date getChatActivityFinalMessageDate() {
//        return chatActivityFinalMessageDate;
//    }

//    public void setChatActivityFinalMessageDate(Date chatActivityFinalMessageDate) {
//        this.chatActivityFinalMessageDate = chatActivityFinalMessageDate;
//    }

    public static void fetchedPostsThatIParticipated(final FindCallback<PChatActivity> callback) {
        PUser currentUSer = PUser.getCurrentUser();
        ParseQuery<PChatActivity> query = ParseQuery.getQuery(PChatActivity.class);
//        query.whereNotContainedIn(PNoticeBoard.participants, Arrays.asList(currentUSer));
        query.whereContainedIn(PNoticeBoard.participants, Arrays.asList(currentUSer));
        query.whereNotContainedIn(PChatActivity.usersLeft, Arrays.asList(currentUSer));
        query.orderByDescending(updatedAt);
        query.include(PChatActivity.usersLeft);
        query.include(participants);
        query.findInBackground(new FindCallback<PChatActivity>() {
            @Override
            public void done(List<PChatActivity> pChatActivities, ParseException e) {
//                callback.done(reOrderChatActivityAccordingTotheMessageCreateDate(pChatActivities), e);
                callback.done(pChatActivities, e);
            }
        });
    }

    public static void fetchedNewPostsThatIParticipated(final List<PChatActivity> oldList, final FindCallback<PChatActivity> callback) {
        fetchedPostsThatIParticipated(new FindCallback<PChatActivity>() {
            @Override
            public void done(List<PChatActivity> pChatActivities, ParseException e) {

                List<PChatActivity> res = new ArrayList<PChatActivity>();
                if(e == null){

                    for (int i = 0; i < pChatActivities.size(); i++) {
                        String newObjId = pChatActivities.get(i).getObjectId();
                        boolean isInThere = false;
                        for (int j = 0; j < oldList.size(); j++){
                            String oldObjId = oldList.get(j).getObjectId();
                            if(oldObjId.equals(newObjId)){
                                isInThere = true;
                                break;
                            }
                        }
                        if(!isInThere){
                            res.add(pChatActivities.get(i));
                        }
                    }
                    callback.done(res,e);
                }else{
                    callback.done(res,e);
                }
            }
        });
    }

//    private static List<PChatActivity> reOrderChatActivityAccordingTotheMessageCreateDate(List<PChatActivity> pChatActivities) {
//        for(PChatActivity activity : pChatActivities){
//            List<GenericMessage> messageList =activity.getAllMessages();
//            activity.setChatActivityFinalMessageDate(messageList.get(messageList.size()-1).getCreatedAt());
//        }
//        Collections.sort(pChatActivities, new Comparator<PChatActivity>() {
//            @Override
//            public int compare(PChatActivity lhs, PChatActivity rhs) {
//                Date date1 = lhs.getChatActivityFinalMessageDate();
//                Date date2 = rhs.getChatActivityFinalMessageDate();
//                return date2.compareTo(date1);
//            }
//        });
//        return pChatActivities;
//    }

    public void putMeInLeftArray() {
        PUser currentUser = PUser.getCurrentUser();
        addUserLeft(currentUser);
    }

    public static void fetchedOneToOneMessagesThatIParticipatedWhitThisUser(
            PUser user,final GetCallback<PChatActivity> callback){

        PUser currentUSer = PUser.getCurrentUser();

        ParseQuery<PChatActivity> query = ParseQuery.getQuery(PChatActivity.class);
//        query.whereContainedIn(PChatActivity.participants, Arrays.asList(currentUSer,user));
        query.whereContainsAll(PChatActivity.participants,Arrays.asList(currentUSer,user));
        query.whereEqualTo(PChatActivity.chatType, "single");
        query.include(PChatActivity.usersLeft);
        query.getFirstInBackground(new GetCallback<PChatActivity>() {
            @Override
            public void done(PChatActivity pChatActivity, ParseException e) {
                callback.done(pChatActivity,e);
            }
        });
    }

    public static void createNewChatWithThisUser(PUser user, GenericMessage msg, final GenericCallBack callback) {
        PUser currentUser = PUser.getCurrentUser();
        final PChatActivity chatActivity = new PChatActivity();
        chatActivity.addMessage(msg);
        chatActivity.putParticipants(Arrays.asList(currentUser, user));
        chatActivity.setChatType("single");
        chatActivity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(chatActivity, e);
            }
        });
    }

    public void reArrangeTheLeftUserArray(PUser user) {
        List<PUser> userList = this.getUserleft();
        PUser currentUser = PUser.getCurrentUser();
        LOGD(TAG, "reArrangeTheLeftUserArray");

        if(userList == null)
            return;

        for (PUser u : userList){
            if(u.getObjectId().equals(user.getObjectId()))
                userList.remove(u);
            this.put(PChatActivity.usersLeft, userList);
        }

        for (PUser u : userList){
            if(u.getObjectId().equals(currentUser.getObjectId()))
                userList.remove(u);
            this.put(PChatActivity.usersLeft, userList);
        }
    }

    public static void fetchNewMessages(
            final GenericMessage finalMsg ,PChatActivity post, final MessagesResultCallback<GenericMessage> messagesResultCallback) {

        post.fetchInBackground(new GetCallback<PChatActivity>() {
            @Override
            public void done(PChatActivity pChatActivity, ParseException e) {
                if(e == null){
                    List<GenericMessage> messageList = pChatActivity.getAllMessages();
                    if(messageList.size() > 0){
                        List<GenericMessage> newMessages = getNewMessagesFromList(finalMsg, messageList);
                        messagesResultCallback.done(newMessages,e);
                    }else{
                        messagesResultCallback.done(messageList, e);
                    }
                }else{
                    messagesResultCallback.done(null,e);
                }
            }
        });


    }

    private static List<GenericMessage> getNewMessagesFromList(GenericMessage finalMsg, List<GenericMessage> messageList) {
        List<GenericMessage> res = new ArrayList<GenericMessage>();

        for (GenericMessage message : messageList){
            long lastMsgDate = finalMsg.getCreatedAt().getTime();
            long newMsgDate = message.getCreatedAt().getTime();

            if(newMsgDate > lastMsgDate)
                res.add(message);
        }

        return res;
    }

    public static void addUserToTheChatParticipantArray(final PUser selectedUser, PChatActivity post, final ResultCallback callback) {
        ParseQuery<PChatActivity> query = ParseQuery.getQuery(PChatActivity.class);
        query.whereEqualTo(objectId, post.getObjectId());
        query.getFirstInBackground(new GetCallback<PChatActivity>() {
            @Override
            public void done(PChatActivity pChatActivity, ParseException e) {
                if(e == null){
                    List<PUser> userList = pChatActivity.getParticipants();
                    boolean isUserInParticipantArray =false;
                    for(PUser pUser : userList){
                        if(pUser.getObjectId().equals(selectedUser.getObjectId())){
                            isUserInParticipantArray = true;
                            break;
                        }
                    }

                    List<PUser> leftUserList = pChatActivity.getUserleft();
                    boolean isUserInLeftUserList = false;
                    if(leftUserList != null){
                        for (PUser u : leftUserList){
                            if(u.getObjectId().equals(selectedUser.getObjectId())){
                                leftUserList.remove(u);
                                isUserInLeftUserList = true;
                            }
                        }
                    }

                    if(isUserInLeftUserList)
                        pChatActivity.put(PChatActivity.usersLeft, leftUserList);

                    if(!isUserInParticipantArray){
                        pChatActivity.addParticipant(selectedUser);
                        pChatActivity.setChatType("group");
                    }

                    if(isUserInLeftUserList || !isUserInParticipantArray){
                        pChatActivity.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                callback.done(true,e);
                            }
                        });
                    }else
                        callback.done(false,e);

                }else
                {
                    callback.done(false,e);
                }

            }
        });
    }


    //    public List<GenericMessage> __getAllMessages() {
//        Gson gson = new Gson();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        JSONArray array = getJSONArray(messages);

//        List<GenericMessage> messageList = getList(messages);



//        if(objArray == null)
//            return  null;
//
//        String str = array.toString();
//        List<GenericMessage> chatMessageList =
//                gson.fromJson(str, new TypeToken<ArrayList<GenericMessage>>(){}.getType());
//
//
//        for(GenericMessage msg : chatMessageList){
//            String _iso = msg.getNSDate().getIso();
//            String iso = _iso.substring(0, _iso.length() - 1).trim();
//
////            msg.setCreatedAt(getDate(msg.getNSDate()));
////            try {
//////                msg.setCreatedAt(formatter.parse(iso));
////                msg.setCreatedAt(new Date(iso));
////            } catch (java.text.ParseException e) {
////                e.printStackTrace();
////                LOGD(TAG, "getAllMessages-ParseException e:" + e.getMessage());
////            }
//        }
//        List<ChatMessage> chatMessageList = generateClassFromJSon(array);
//        if(chatMessageList!=null)
//            return chatMessageList;
//        else
//            return null;
//    }

}
