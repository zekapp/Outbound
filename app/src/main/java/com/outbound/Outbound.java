package com.outbound;

import android.app.Application;
import android.util.Log;

import com.outbound.model.PChatActivity;
import com.outbound.model.PEvent;
import com.outbound.model.PFriendRequest;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PTrip;
import com.outbound.model.PUser;
import com.outbound.util.GenericMessage;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by zeki on 25/08/2014.
 */
public class Outbound extends Application {

    //singleton
    private  static Outbound instance = null;
    //for double check locking
    private static Object lock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

//        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(PUser.class);
        ParseObject.registerSubclass(PFriendRequest.class);
        ParseObject.registerSubclass(PEvent.class);
        ParseObject.registerSubclass(PTrip.class);
        ParseObject.registerSubclass(PNoticeBoard.class);
        ParseObject.registerSubclass(PChatActivity.class);
//        PushService.setDefaultPushCallback(this, DispatchActivity.class);

        // Required - Initialize the Parse SDK
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
//        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        

        instance = this;

    }
    public static Outbound getInstance(){
        Log.i("getInstance", "called by");
        if(instance == null){
            synchronized (lock){
                if(instance == null){
                    instance = new Outbound();
                }
            }
        }
        Log.i("getInstance", "instance returned" );
        return  instance;
    }
}
