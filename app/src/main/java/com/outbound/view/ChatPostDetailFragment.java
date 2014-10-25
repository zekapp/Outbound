package com.outbound.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PChatActivity;
import com.outbound.model.PUser;
import com.outbound.ui.util.adapters.ChatMessageListViewAdapter;
import com.outbound.util.ConnectionDetector;
import com.outbound.util.Constants;
import com.outbound.util.GenericCallBack;
import com.outbound.util.GenericMessage;
import com.outbound.util.MessagesResultCallback;
import com.outbound.util.TimeUtil;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 21/10/2014.
 */
public class ChatPostDetailFragment extends BaseFragment {
    private static final String TAG = makeLogTag(ChatPostDetailFragment.class);

    private TextView actionBarTitle;
    private PChatActivity post = null;
    private List<GenericMessage> messageList = null;//this is the real message list. Adapter contain dublicate message for timestamp
    private  View rootView;
    private ChatMessageListViewAdapter mAdapter = null;
    private PUser user = null;

    private EditText messageEditText;
    private RelativeLayout sendMessageBtn;
    private ProgressBar mProgressView;
    private View sendButtonView;
    private TextView timeStamp;

    private ListView mListView;
    private ConnectionDetector detector;
    private Runnable newMessageAction;

    private static  boolean firstInitializeDone = false;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);
        if(param1 instanceof PChatActivity){
            post = (PChatActivity)param1;
            messageList = post.getAllMessages();
        }else if( param1 instanceof  PUser){
            user = (PUser)param1;
            messageList = null;
            post = null;

            if(mAdapter != null)
                mAdapter.clear();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mCallbacks != null){
            mCallbacks.hideTabbar();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mCallbacks != null){
            mCallbacks.showTabbar();
        }
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        actionBarTitle = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        if(messageList != null)
            actionBarTitle.setText(messageList.get(0).getUserName());
        else if(user != null)
            actionBarTitle.setText(user.getUserName());
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add_user);
        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallbacks !=null)
                    mCallbacks.backIconClicked();
            }
        });

        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new ConnectionDetector(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setUpActionBar(getActivity());
        rootView= inflater.inflate(R.layout.message_activity, container, false);

        sendMessageBtn = (RelativeLayout)rootView.findViewById(R.id.ma_send_message);
        messageEditText = (EditText)rootView.findViewById(R.id.ma_edit_message_text);
        mProgressView = (ProgressBar)rootView.findViewById(R.id.send_progress);
        mProgressView.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_accent_1), android.graphics.PorterDuff.Mode.MULTIPLY);
        sendButtonView = rootView.findViewById(R.id.send_button_img);
        timeStamp  = (TextView)rootView.findViewById(R.id.timestamp);

        setUpListView();
        setUpSentTextButton();

        setUpListViewAction();

        setUpNewMessageCheckThread();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(newMessageAction != null && mListView != null)
            mListView.removeCallbacks(newMessageAction);
    }

    private void setUpNewMessageCheckThread() {

        if(mListView != null){
            newMessageAction = new Runnable() {
                @Override
                public void run() {
                    if(mAdapter != null && firstInitializeDone && post != null){
                        LOGD(TAG, "setUpNewMessageCheckThread called");
                        int count = mAdapter.getCount();
                        GenericMessage msg = mAdapter.getItem(count-1); //final message
                        //fetch all message that time stamp is greater than final message
                        PChatActivity.fetchNewMessages(msg,post,new MessagesResultCallback<GenericMessage>() {
                            @Override
                            public void done(List<GenericMessage> newMessages, Exception e) {
                                if(e == null){
                                    if(newMessages.size() > 0){
                                        for(GenericMessage newMsg : newMessages)
                                            addNewMessageToTheAdapter(newMsg);
                                    }
                                }
                            }
                        });
                    }
                    mListView.postDelayed(this,5000);
                }
            };

            mListView.post(newMessageAction);
        }
    }

    private void setUpListViewAction() {
//        newMessageCheckAction =  new Runnable() {
//            @Override
//            public void run() {
//                PChatActivity.fetchNewMessages(mAdapter.getCount(),post,new MessagesResultCallback<GenericMessage>() {
//                    @Override
//                    public void done(List<GenericMessage> newMessages, Exception e) {
//                        if(e == null){
//                            LOGD(TAG, "fetchNewMessages size: " + newMessages.size());
//                            if(newMessages.size() > 0){
////                                mAdapter.addAll(newMessages);
////                                updateView();
//                            }
//                        }else
//                        {
//                            LOGD(TAG, "network error do not warn user e: " + e.getMessage());
//                        }
//                    }
//                });
//                mListView.postDelayed(this,  5000); // check for new message every 5 second if this frame is active
//            }
//        };
    }

    private void setUpListView() {
        mListView = (ListView)rootView.findViewById(R.id.message_list);
        if(mAdapter == null)
            mAdapter = new ChatMessageListViewAdapter(getActivity());

        firstInitializeDone = false;
        feedTheAdapter();
        updateView();
        mAdapter.addOnItemclickedListener(new ChatMessageListViewAdapter.OnChatMessageItemClickedListener() {
            @Override
            public void profilePictureClicked(PUser user) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID,user,null);
            }
        });
        mListView.setAdapter(mAdapter);

        //not in use anymore.
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mAdapter.isEmpty())
                    return;

                GenericMessage msg = mAdapter.getItem(firstVisibleItem);
                if(msg != null)
                    timeStamp.setHint(TimeUtil.convertDateTimeFormat(msg.getCreatedAt()));
            }
        });

//        mAdapter = new NoticeBoardMessageDetailAdapter(getActivity());
//        mAdapter.addOnNoticeBoardMsgItemClickedListener(this);
//        feedTheAdapter();
//        mListView.setAdapter(mAdapter);

//        setUpListViewAction();

    }

    private void setUpSentTextButton(){
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detector.isConnectingToInternet()){
                    showProgress(true);
                    final GenericMessage msg;
                    msg = generateTheMessage();

                    if(messageList == null){
                        // ChatActivity not created. create it with message and user
                        // after creating successfully, init messageList = new ArrayList<GenericMessage>();
                        //feed

                        PChatActivity.createNewChatWithThisUser(user,msg,new GenericCallBack() {
                            @Override
                            public void done(Object res, ParseException e) {
                                showProgress(false);
                                if(e == null){
                                    post = (PChatActivity)res;
                                    addNewMessageToTheAdapter(msg);
//                                    feedAdapterCorrectTypeOrder();
                                    messageEditText.setText("");
                                }else
                                {
                                    showToastMessage("Network Error. Check your connection...");
                                    LOGD(TAG, "createNewChatWithThisUser e " + e.getMessage());
                                }
                            }
                        });
                    }else{
                        // there is post and messagelist
                        if(user != null){
                            //it means this fragment deployed from person profile
                            if(post.getChatType().equals("single"))
                                post.reArrangeTheLeftUserArray(user);
                        }

                        post.addMessage(msg);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                showProgress(false);
                                if(e == null){
                                    messageEditText.setText("");
                                    addNewMessageToTheAdapter(msg);
                                }else{
                                    showToastMessage("Network Error. Check your connection...");
                                    LOGD(TAG, "setUpSentTextButton-post.saveInBackground e: " + e.getMessage());
                                }
                            }
                        });
                    }
                }else
                {
                    showToastMessage("Check your internet connection...");
                }
            }
        });
    }

    private void addNewMessageToTheAdapter(GenericMessage msg) {
        PUser currentUser = PUser.getCurrentUser();
        if(messageList == null){
            messageList = new ArrayList<GenericMessage>();
        }
        messageList.add(msg);

        if(mAdapter != null){
            if(mAdapter.isEmpty()){
                feedAdapterCorrectTypeOrder();
            }else
            {
                GenericMessage lastMessage = mAdapter.getItem(mAdapter.getCount()-1);
                int dayBetween = TimeUtil.daysBetween(msg.getCreatedAt(), lastMessage.getCreatedAt());
                if(dayBetween > 0)
                    mAdapter.addTimeStampView(msg);

                if(msg.getUserID().equals(currentUser.getObjectId())){
                    mAdapter.addItemRight(msg);
                }else{
                    mAdapter.addItemleft(msg);
                }
            }
        }

        updateView();
    }

    private GenericMessage generateTheMessage() {
        PUser currentUser = PUser.getCurrentUser();
        GenericMessage message = new GenericMessage();
        message.setUserName(currentUser.getUserName());
        message.setUserID(currentUser.getObjectId());
        message.setProfilePicture(currentUser.getProfilePicture());
        message.setMessage(messageEditText.getText().toString());
        message.setCreatedAt(new Date());
        return message;
    }

    private void feedTheAdapter(){
//  iniztialize at the first creating of this view. dont use later
//  use addNewMessageToTheAdapter instead
        if(messageList != null) {
            // if profile message clicked
            feedAdapterCorrectTypeOrder();
            firstInitializeDone = false;
        }else{
            startProgress("Feching previous messages...");
            if(user==null)
                LOGE(TAG, "user should not be nulll ");

            PChatActivity.fetchedOneToOneMessagesThatIParticipatedWhitThisUser(user, new GetCallback<PChatActivity>() {
                @Override
                public void done(PChatActivity pChatActivity, ParseException e) {
                    stopProgress();
                    firstInitializeDone = false;
                    if (e == null) {
                        //you have already one chat with this user. continue with it
                        post = pChatActivity; //must be initiliazed with messageList
                        messageList = pChatActivity.getAllMessages(); //must be initiliazed with post
                        feedAdapterCorrectTypeOrder();
                    } else {
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            //no chat found messageList and post is still null
                            showToastMessage("No previous messages found...");
                            LOGD(TAG, "feedTheAdapter OBJECT_NOT_FOUND " + e.getMessage());
                        } else {
                            showToastMessage("Network Error...");
                            LOGD(TAG, "fetchedMessagesThatIParticipatedWhitThisUser e: " + e.getMessage());
                        }
                    }
                }
            });
        }
    }

    private void feedAdapterCorrectTypeOrder() {

        if(messageList == null)
            return;

        PUser currentUser = PUser.getCurrentUser();
        mAdapter.clear();
        for(int i=0;i<messageList.size();i++){
            GenericMessage message = messageList.get(i);
            if(i == 0){
                mAdapter.addTimeStampView(message);
            }else{
                GenericMessage messagePrev = messageList.get(i-1);
                int dayBetween = TimeUtil.daysBetween(message.getCreatedAt(), messagePrev.getCreatedAt());
                if(dayBetween > 0)
                    mAdapter.addTimeStampView(message);
            }

            if(message.getUserID().equals(currentUser.getObjectId())){
                mAdapter.addItemRight(message);
            }else{
                mAdapter.addItemleft(message);
            }
        }
        updateView();
    }

    private void updateView() {
        if(mAdapter != null && mListView != null){
            mAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mAdapter.getCount()-1);
        }
    }

    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            sendMessageBtn.setEnabled(!show);

            sendButtonView.setVisibility(show ? View.GONE : View.VISIBLE);
            sendButtonView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sendButtonView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            sendButtonView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    //Algorithm
//    private void __feedTheAdapter() {
//
//        PUser currentUser = PUser.getCurrentUser();
//        if(mAdapter != null && messageList != null){
//            mAdapter.clear();
//
//            //0 loop
//            GenericMessage message1 = messageList.get(0);
//            mAdapter.addTimeStampView(message1);
//            if(message1.getUserID().equals(currentUser.getObjectId())){
//                mAdapter.addItemRight(message1);
//            }else{
//                mAdapter.addItemleft(message1);
//            }
//
//            // 1 loop
//            GenericMessage message2 = messageList.get(1);
//            int daysBetween1 = TimeUtil.daysBetween(message2.getCreatedAt(), message1.getCreatedAt());
//
//            if(daysBetween1 > 0)
//                mAdapter.addTimeStampView(message2);
//
//            if(message2.getUserID().equals(currentUser.getObjectId())){
//                mAdapter.addItemRight(message2);
//            }else{
//                mAdapter.addItemleft(message2);
//            }
//
//            //2 loop
//            GenericMessage message3 = messageList.get(2);
//            int daysBetween2 = TimeUtil.daysBetween(message3.getCreatedAt(), message2.getCreatedAt());
//
//            if(daysBetween2 > 0)
//                mAdapter.addTimeStampView(message3);
//
//            if(message2.getUserID().equals(currentUser.getObjectId())){
//                mAdapter.addItemRight(message3);
//            }else{
//                mAdapter.addItemleft(message3);
//            }
//
//            mAdapter.addAll(messageList);
//            updateView();
//        }
//    }
//
//    private void _feedTheAdapter() {
//        PUser currentUser = PUser.getCurrentUser();
//        if(mAdapter != null && messageList != null){
//            mAdapter.clear();
//            for (GenericMessage msg : messageList){
//                if(msg.getUserID().equals(currentUser.getObjectId())){
//                    mAdapter.addItemRight(msg);
//                }else{
//                    mAdapter.addItemleft(msg);
//                }
//            }
//            mAdapter.addAll(messageList);
//            updateView();
//        }
//    }
}
