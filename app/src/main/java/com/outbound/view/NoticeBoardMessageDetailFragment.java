package com.outbound.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.NoticeBoardMessage;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.ui.util.adapters.NoticeBoardMessageDetailAdapter;
import com.outbound.util.ConnectionDetector;
import com.outbound.util.Constants;
import com.outbound.util.GenericMessage;
import com.outbound.util.MessagesResultCallback;
import com.outbound.util.ResultCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 19/10/2014.
 */
public class NoticeBoardMessageDetailFragment extends BaseFragment implements NoticeBoardMessageDetailAdapter.OnNoticeBoardMsgItemClickedListener {
    private static final String TAG = makeLogTag(NoticeBoardMessageDetailFragment.class);

    private TextView actionBarTitle;
    private EditText messageEditText;
    private PNoticeBoard post;

    private ListView mListView;
    private NoticeBoardMessageDetailAdapter mAdapter ;
    private RelativeLayout sendMessageBtn;
    private ConnectionDetector detector;
    private InputMethodManager imm;
    private ProgressBar mProgressView;
    private View sendButtonView;
    private  View rootView;
    private View headerView;

    private Runnable newMessageCheckAction;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new ConnectionDetector(getActivity());
    }

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);

        if(param1 instanceof PNoticeBoard)
            post = (PNoticeBoard)param1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mCallbacks != null){
            mCallbacks.hideTabbar();
        }
        imm = (InputMethodManager)activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
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
        actionBarTitle.setText(post.getNoticeboardTitle());
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.icon_active);
//        icon.setVisibility(View.GONE);

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setUpActionBar(getActivity());
        rootView= inflater.inflate(R.layout.notice_board_message_detail, container, false);
        headerView = inflater.inflate(R.layout.item_notice_board_message_header_activity, null);
        createHeaderView();

        sendMessageBtn = (RelativeLayout)rootView.findViewById(R.id.nb_send_message_button);
        messageEditText = (EditText)rootView.findViewById(R.id.ma_edit_message_text);
        mProgressView = (ProgressBar)rootView.findViewById(R.id.send_progress);
        mProgressView.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_accent_1), android.graphics.PorterDuff.Mode.MULTIPLY);
        sendButtonView = rootView.findViewById(R.id.send_button_img);
        setUpListView();
        setUpSentTextButton();
        return rootView;
    }

    private void setUpSentTextButton() {

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detector.isConnectingToInternet()){
                    final GenericMessage msg;
                    try {
                        showProgress(true);
                        msg = generateTheMessage();
                        post.setMessages(msg);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                showProgress(false);
                                if(e == null){
                                    mAdapter.add(msg);
                                    updateView();
                                    mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
                                    messageEditText.setText("");
                                    imm.hideSoftInputFromInputMethod(sendMessageBtn.getWindowToken(),0);
                                }else
                                {
                                    showToastMessage("Network error. Check your connection");
                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LOGD(TAG, "setUpSentTextButton: " + e.getMessage());
                        showToastMessage("Error occurred " + e.getMessage());
                    }

                }else
                {
                    showToastMessage("Check your internet connection...");
                }
            }
        });
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
    private NoticeBoardMessage _generateTheMessage() {
        NoticeBoardMessage message = new NoticeBoardMessage();
        PUser currentUser = PUser.getCurrentUser();
        message.setUserName(currentUser.getUserName());
        message.setUserID(currentUser.getObjectId());
//                    msg.setDate(new Date());
        message.setDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        message.setProfilePicture(currentUser.getProfilePicture());
        message.setText(messageEditText.getText().toString());
        return message;
    }

    private void setUpListView() {
        mListView = (ListView)rootView.findViewById(R.id.nb_message_list);
        mListView.addHeaderView(headerView, null, false);
        mListView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.gray)));
        mListView.setDividerHeight(1);
        mAdapter = new NoticeBoardMessageDetailAdapter(getActivity());
        mAdapter.addOnNoticeBoardMsgItemClickedListener(this);
        feedTheAdapter();
        mListView.setAdapter(mAdapter);

        setUpListViewAction();

    }

    private void setUpListViewAction() {
        newMessageCheckAction =  new Runnable() {
            @Override
            public void run() {

                PNoticeBoard.fetchNewMessages(mAdapter.getCount(),post,new MessagesResultCallback<GenericMessage>() {
                    @Override
                    public void done(List<GenericMessage> newMessages, Exception e) {
                        if(e == null){
                            LOGD(TAG, "fetchNewMessages size: " + newMessages.size());
                            if(newMessages.size() > 0){
                                mAdapter.addAll(newMessages);
                                updateView();
                            }
                        }else
                        {
                            LOGD(TAG, "network error do not warn user e: " + e.getMessage());
                        }
                    }
                });
                mListView.postDelayed(this,  5000); // check for new message every 5 second if this frame is active
            }
        };
        mListView.post(newMessageCheckAction);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(newMessageCheckAction != null  && mListView != null)
            mListView.removeCallbacks(newMessageCheckAction);
    }

    private TextView report;
    private TextView block;
    private TextView description;
    private static  boolean descriptionFieldExpanded = false;
    private void createHeaderView(){
        RoundedImageView photo = (RoundedImageView)headerView.findViewById(R.id.nb_user_photo);
        TextView nameSurname = (TextView)headerView.findViewById(R.id.nb_name_of_creater);
        description = (TextView)headerView.findViewById(R.id.nb_message);
        TextView createdDate = (TextView)headerView.findViewById(R.id.nb_create_date);
        report = (TextView)headerView.findViewById(R.id.nb_report);
        block = (TextView)headerView.findViewById(R.id.nb_block);


        photo.setParseFile(post.getCreatedBy().getProfilePicture());
        photo.loadInBackground();
        nameSurname.setText(post.getCreatedBy().getUserName());
        description.setHint(post.getDescription());
        createdDate.setHint(new SimpleDateFormat("dd/MM/yy").format(post.getCreatedAt()));
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReportDialog();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBlockDialog();
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descriptionFieldExpanded){
                    descriptionFieldExpanded = false;
                    description.setMaxLines(5);
                }else{
                    descriptionFieldExpanded = true;
                    description.setMaxLines(105);
                }
               if(mAdapter != null)
                   mAdapter.notifyDataSetChanged();

            }
        });

    }

    private void feedTheAdapter() {
        List<GenericMessage> messageList = post.getAllMessages();

        if(mAdapter != null && messageList != null){
            mAdapter.clear();
            mAdapter.addAll(messageList);
            updateView();
        }
    }

    private void updateView() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void profilePictureClicked(PUser user) {
        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID, user, null);
    }

    private void enableButtons() {
        block.setEnabled(true);
        report.setAlpha((float) 1);
        block.setAlpha((float) 1);
    }

    private void disableButtons() {
        report.setEnabled(false);
        block.setEnabled(false);
        report.setAlpha((float) 0.2);
        block.setAlpha((float) 0.2);
    }


    private void openReportDialog() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Do you want to report \""+ post.getCreatedBy().getUserName()+"\" ?");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //start meail activity
                dialog.dismiss();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private void openBlockDialog() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Do you want to block \""+ post.getCreatedBy().getUserName()+"\" ?");
        ad.setMessage("When you block this user, this user will never send you message and never join your events");
        ad.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disableButtons();
                showToastMessage(post.getCreatedBy().getUserName() + " blocking...");

                PUser.blockThisUser(post.getCreatedBy(), new ResultCallback() {
                    @Override
                    public void done(boolean res, ParseException e) {
                        enableButtons();
                        if(e == null){
//                            remove(noticeBoardMessage);
//                            notifyDataSetChanged();
                            //deploy noticeboard.
                            showToastMessage(post.getCreatedBy().getUserName() + " blocked");
                        }else
                        {
                            LOGD(TAG, "openBlockDialog - blockThisUser e: " + e.getMessage());
                            showToastMessage("Network Error. Check your connection");
                        }
                    }
                });

                dialog.dismiss();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }
}
