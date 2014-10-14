package com.outbound.model;

import com.outbound.util.ResultCallback;
import com.outbound.util.location.LocationUtils;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 9/10/2014.
 */
@ParseClassName("FriendRequest")
public class PFriendRequest extends ParseObject {
    private static final String TAG = makeLogTag(PFriendRequest.class);
    public static final int PENDIGN = 0;
    public static final int ACCEPTED = 1;
    public static final int DECLINED = 2;

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


    public static void findPendingCursrentUserRequest(PUser user, final FindCallback<PUser> callback) {
        PUser currentUser = PUser.getCurrentUser();

        ParseQuery<PFriendRequest> innerQuery = ParseQuery.getQuery(PFriendRequest.class);
        innerQuery.whereEqualTo(strStatus,strStatusPending);
        innerQuery.include(PFriendRequest.strReceiver);

        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
        query.whereMatchesQuery(PUser.strObjectId,innerQuery);

        query.findInBackground(new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pFriendRequests, ParseException e) {
                List<PUser> userList = new ArrayList<PUser>();

                for(PUser pUser:pFriendRequests){
                    userList.add(pUser);
                }
                callback.done(userList,e);
            }
        });
    }


    public static void findFriends(final PUser user, final FindCallback<PUser> callback ) {

        ParseQuery<PFriendRequest> queryReceiver = ParseQuery.getQuery(PFriendRequest.class);
        queryReceiver.whereEqualTo(strStatus, strStatusAccepted);
        queryReceiver.whereEqualTo(strReceiver, user);


        ParseQuery<PFriendRequest> queryFrom = ParseQuery.getQuery(PFriendRequest.class);
        queryFrom.whereEqualTo(strStatus, strStatusAccepted);
        queryFrom.whereEqualTo(strForwarder, user);


        List<ParseQuery<PFriendRequest>> queries = new ArrayList<ParseQuery<PFriendRequest>>();
        queries.add(queryReceiver);
        queries.add(queryFrom);


        ParseQuery<PFriendRequest> mainQuery = ParseQuery.or(queries);
        mainQuery.include(strReceiver);
        mainQuery.include(strForwarder);

        mainQuery.findInBackground(new FindCallback<PFriendRequest>() {
            @Override
            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                List<PUser> pUsers = new ArrayList<PUser>();
                LOGD(TAG, "friends count: " + pFriendRequests.size());
                for (PFriendRequest pFriendRequest : pFriendRequests) {
                    if (!pFriendRequest.getReceiver().getObjectId().equals(user.getObjectId()))
                        pUsers.add(pFriendRequest.getReceiver());
                    else
                        pUsers.add(pFriendRequest.getForwarder());
                }

                callback.done(LocationUtils.orderFriendsAccordingDistance(pUsers), e);

                LOGD(TAG, "findFriends e: " + ((e != null) ? e.getMessage() : "null"));
            }
        });
    }

    public static  void sendFriendRequest(PUser requestSender, final SaveCallback callback) {
        PUser currentUser = PUser.getCurrentUser();
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



    public static void findPendingFriends(final PUser user, final FindCallback<PUser> findCallback) {
        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
        query.whereEqualTo(strStatus, strStatusPending);
        query.whereEqualTo(strReceiver, user);
        query.include(strForwarder);
        query.findInBackground(new FindCallback<PFriendRequest>() {
            @Override
            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                List<PUser> userList = new ArrayList<PUser>();
                if(e == null){
                    for(PFriendRequest request : pFriendRequests){
                        userList.add(request.getForwarder());
                    }
                }

                findCallback.done(LocationUtils.orderFriendsAccordingDistance(userList),e);
            }
        });

    }

    public static void isThisUserAlreadyFriend(PUser user, final ResultCallback callback) {
        PUser currentUser = PUser.getCurrentUser();

        if(user.getObjectId().equals(currentUser.getObjectId())){
            callback.done(true, null);
            return;
        }

        ParseQuery<PFriendRequest> query1 = ParseQuery.getQuery(PFriendRequest.class);
        query1.whereEqualTo(strStatus, strStatusAccepted);
        query1.whereEqualTo(strReceiver,user);
        query1.whereEqualTo(strForwarder,currentUser);


        ParseQuery<PFriendRequest> query2 = ParseQuery.getQuery(PFriendRequest.class);
        query2.whereEqualTo(strStatus, strStatusAccepted);
        query2.whereEqualTo(strForwarder,user);
        query2.whereEqualTo(strReceiver,currentUser);

        ParseQuery<PFriendRequest> query3 = ParseQuery.getQuery(PFriendRequest.class);
        query3.whereEqualTo(strStatus, strStatusPending);
        query3.whereEqualTo(strReceiver,user);
        query3.whereEqualTo(strForwarder,currentUser);


        ParseQuery<PFriendRequest> query4 = ParseQuery.getQuery(PFriendRequest.class);
        query4.whereEqualTo(strStatus, strStatusPending);
        query4.whereEqualTo(strForwarder,user);
        query4.whereEqualTo(strReceiver,currentUser);

        List<ParseQuery<PFriendRequest>> queries = new ArrayList<ParseQuery<PFriendRequest>>();
        queries.add(query1);
        queries.add(query2);
        queries.add(query3);
        queries.add(query4);

        ParseQuery<PFriendRequest> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<PFriendRequest>() {
            @Override
            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                if(pFriendRequests.size() > 0)
                    callback.done(true , e);
                else
                    callback.done(false,e);
            }
        });
    }
}

//    public static void findFriends(final int status,final PUser user, final FindCallback<PUser> callback ){
        //RUNNINING INNER QUERY
//        PUser currentUser = PUser.getCurrentUser();
//        ParseQuery<PUser> innerQuery = ParseQuery.getQuery(PUser.class);
//        innerQuery.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
//
//        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
//        query.whereMatchesQuery("toUser", innerQuery);
//
//        query.findInBackground(new FindCallback<PFriendRequest>() {
//            @Override
//            public void done(List<PFriendRequest> pUsers, ParseException e) {
////                callback.done(pUsers,e);
//
//                LOGD(TAG, "array size: " + ((pUsers!=null)?pUsers.size():"array Null") );
//                LOGD(TAG, "exception: " + ((e!=null)?e.getMessage():"no error") );
//            }
//        });



//        PUser currentUser = PUser.getCurrentUser();
//
//        ParseQuery<PFriendRequest> innerQuery = ParseQuery.getQuery(PFriendRequest.class);
//        innerQuery.whereEqualTo(strStatus,ACCEPTED);
//        innerQuery.whereEqualTo(strReceiver,user);
//
//
//        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
////        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
//        query.whereMatchesKeyInQuery("objectId","toUser",innerQuery);
//
//        query.findInBackground(new FindCallback<PUser>() {
//            @Override
//            public void done(List<PUser> pUsers, ParseException e) {
//                callback.done(pUsers,e);
//
//                LOGD(TAG, "array size: " + ((pUsers!=null)?pUsers.size():"array Null") );
//                LOGD(TAG, "exception: " + ((e!=null)?e.getMessage():"no error") );
//            }
//        });


//        PUser currentUser = PUser.getCurrentUser();
//        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
//        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
//        query.findInBackground(new FindCallback<PUser>() {
//            @Override
//            public void done(final List<PUser> pUsers, ParseException e) {
//                if(e == null){
//                    ParseQuery<PFriendRequest> innerQuery = ParseQuery.getQuery(PFriendRequest.class);
//                    innerQuery.whereEqualTo(strStatus,
//                            status==PENDIGN?strStatusPending:status==ACCEPTED?
//                                    strStatusAccepted:strStatusDeclined);
//                    innerQuery.whereEqualTo(status==PENDIGN?strReceiver:strForwarder,user);
//                    innerQuery.findInBackground(new FindCallback<PFriendRequest>() {
//                        @Override
//                        public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
//                            if(e == null){
//                                List<PUser> userList = new ArrayList<PUser>();
//                                for (int i=0; i< pUsers.size();i++){
//                                    PUser pUser = pUsers.get(i);
//                                    for(int j=0;j<pFriendRequests.size();j++){
////                                    userList.add(pUser);
//                                        PFriendRequest request = pFriendRequests.get(j);
//                                        if(pUser.getObjectId().equals(request.getReceiver().getObjectId())){
//                                            userList.add(pUser);
//                                        }
//                                    }
//                                }
//                                callback.done(userList,e);
//                            }
//                            else
//                                callback.done(null,e);
//
//                        }
//                    });
//                }else
//                    callback.done(null,e);
//            }
//        });
//
//    }



//    public static void findAcceptedCurrentUserRequest(final PUser user, final FindCallback<PUser> callback){
//
//        PUser currentUser = PUser.getCurrentUser();
//        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
//        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
//        query.findInBackground(new FindCallback<PUser>() {
//            @Override
//            public void done(final List<PUser> pUsers, ParseException e) {
//                if(e == null){
//                    ParseQuery<PFriendRequest> innerQuery = ParseQuery.getQuery(PFriendRequest.class);
//                    innerQuery.whereEqualTo(strStatus,strStatusAccepted);
//                    innerQuery.whereEqualTo(strForwarder,user);
//                    innerQuery.findInBackground(new FindCallback<PFriendRequest>() {
//                        @Override
//                        public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
//                            if(e == null){
//                                List<PUser> userList = new ArrayList<PUser>();
//                                for (int i=0; i< pUsers.size();i++){
//                                    PUser pUser = pUsers.get(i);
//                                    for(int j=0;j<pFriendRequests.size();j++){
////                                    userList.add(pUser);
//                                        PFriendRequest request = pFriendRequests.get(j);
//                                        if(pUser.getObjectId().equals(request.getReceiver().getObjectId())){
//                                            userList.add(pUser);
//                                        }
//                                    }
//                                }
//                                callback.done(userList,e);
//                            }
//                            else
//                                callback.done(null,e);
//
//                        }
//                    });
//                }else
//                    callback.done(null,e);
//            }
//        });



//        PUser currentUser = PUser.getCurrentUser();
//
//        ParseQuery<PFriendRequest> innerQuery = ParseQuery.getQuery(PFriendRequest.class);
//        innerQuery.whereEqualTo(strStatus,strStatusAccepted);
//        innerQuery.whereExists(PFriendRequest.strReceiver);
//
//
//        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
//        query.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
//        query.whereMatchesQuery(PUser.strObjectId,innerQuery);
//
//        query.findInBackground(new FindCallback<PUser>() {
//            @Override
//            public void done(List<PUser> pUserList, ParseException e) {
//                List<PUser> userList = new ArrayList<PUser>();
//
//                if(pUserList != null){
//                    for(PUser pUser:pUserList){
//                        userList.add(pUser);
//                    }
//                }
//
//                callback.done(userList,e);
//            }
//        });


//        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
//        query.whereEqualTo(strForwarder, user);
//        query.whereEqualTo(strStatus,strStatusAccepted);
//        query.findInBackground(new FindCallback<PFriendRequest>() {
//            @Override
//            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
//                List<PUser> userList = new ArrayList<PUser>();
//
//                for(PFriendRequest pFriendRequest:pFriendRequests){
//                    userList.add(pFriendRequest.getReceiver());
//                }
//                callback.done(userList,e);
//            }
//        });
//    }



//        PUser currentUser = PUser.getCurrentUser();
//
//        ParseQuery<PUser> innerQuery = ParseQuery.getQuery(PUser.class);
//        innerQuery.whereNear(PUser.strCurrentLocation,currentUser.getCurrentLocation());
//
//        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
//        query.whereEqualTo(strStatus,strStatusPending);
//        query.whereMatchesQuery(strReceiver,innerQuery);
//
//        query.findInBackground(new FindCallback<PFriendRequest>() {
//            @Override
//            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
//                List<PUser> userList = new ArrayList<PUser>();
//
//                for(PFriendRequest pFriendRequest:pFriendRequests){
//                    userList.add(pFriendRequest.getReceiver());
//                }
//                callback.done(userList,e);
//            }
//        });


//        ParseQuery<PFriendRequest> query = ParseQuery.getQuery(PFriendRequest.class);
//        query.whereEqualTo(strForwarder, user);
//        query.whereEqualTo(strStatus,strStatusPending);
//        query.findInBackground(new FindCallback<PFriendRequest>() {
//            @Override
//            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
//                List<PUser> userList = new ArrayList<PUser>();
//
//                for(PFriendRequest pFriendRequest:pFriendRequests){
//                    userList.add(pFriendRequest.getReceiver());
//                }
//                callback.done(userList,e);
//            }
//        });
//    }
