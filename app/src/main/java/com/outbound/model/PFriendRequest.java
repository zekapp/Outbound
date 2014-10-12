package com.outbound.model;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeki on 9/10/2014.
 */
@ParseClassName("FriendRequest")
public class PFriendRequest extends ParseObject {

    public static final String strForwarder = "fromUser";
    public static final String strReceiver  = "toUser";
    public static final String strStatus    = "status";

    public static final String strStatusPending     = "pending";
    public static final String strStatusAccepted    = "accepted";
    public static final String strStatusDeclined    = "declined";

    //Forwarder
    public PUser getForwarder() {
        return (PUser)get(strForwarder);
    }
    public void setForwarder(PUser value) {
        put(strForwarder, value);
    }

    //Receiver
    public PUser getReceiver() {
        return (PUser)get(strReceiver);
    }
    public void setReceiver(PUser value) {
        put(strReceiver, value);
    }

    //Status
    public String getStatus() {
        return getString(strStatus);
    }
    public void setStatus(String value) {
        put(strStatus, value);
    }

    public static void findPendingUsersInBackground(PUser user, final FindCallback<PFriendRequest> callback){
        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
        query.whereEqualTo(strReceiver, user);
        query.whereEqualTo(strStatus,strStatusPending);
        query.findInBackground(new FindCallback<PFriendRequest>() {
            @Override
            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                callback.done(pFriendRequests, e);
            }
        });
    }

    public static void findAcceptedCurrentUserRequest(PUser user, final FindCallback<PUser> callback){
        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
        query.whereEqualTo(strForwarder, user);
        query.whereEqualTo(strStatus,strStatusAccepted);
        query.findInBackground(new FindCallback<PFriendRequest>() {
            @Override
            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                List<PUser> userList = new ArrayList<PUser>();

                for(PFriendRequest pFriendRequest:pFriendRequests){
                    userList.add(pFriendRequest.getReceiver());
                }
                callback.done(userList,e);
            }
        });
    }

    public static void findPendingCurrentUserRequest(PUser user, final FindCallback<PUser> callback) {
        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
        query.whereEqualTo(strForwarder, user);
        query.whereEqualTo(strStatus,strStatusPending);
        query.findInBackground(new FindCallback<PFriendRequest>() {
            @Override
            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                List<PUser> userList = new ArrayList<PUser>();

                for(PFriendRequest pFriendRequest:pFriendRequests){
                    userList.add(pFriendRequest.getReceiver());
                }
                callback.done(userList,e);
            }
        });
    }

    public static  void sendFriendRequest(PUser requestSender,PUser currentUser, final SaveCallback callback) {
        PFriendRequest request = new PFriendRequest();
        request.setForwarder(currentUser);
        request.setReceiver(requestSender);
        request.setStatus(strStatusPending);
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(e);
            }
        });
    }

}
