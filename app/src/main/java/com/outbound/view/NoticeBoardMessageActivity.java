package com.outbound.view;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.ui.util.adapters.NoticeBoardMsgListViewAdapter;
import com.outbound.util.MessageDummyClass;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by zeki on 2/10/2014.
 */
public class NoticeBoardMessageActivity extends FragmentActivity{

    private TextView actionBarTitle;

    private ListView mListView;
    private NoticeBoardMsgListViewAdapter mAdapter ;

    private ArrayList<MessageDummyClass> testMessages = new ArrayList<MessageDummyClass>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_activity);
        setupActionBar();
        setUpListView();
        setUpSentTextButton();
    }

    private void setUpSentTextButton() {
        RelativeLayout sendMessageBtn = (RelativeLayout)findViewById(R.id.ma_send_message);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    private void setUpListView() {

        mListView = (ListView)findViewById(R.id.message_list);
        mListView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.gray)));
        mListView.setDividerHeight(1);
        mAdapter = new NoticeBoardMsgListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        generateTestData();
    }

    private void generateTestData(){
        for (int i = 0; i < 50; i++) {
            MessageDummyClass ms = new MessageDummyClass();
            mAdapter.add(ms);
        }
    }
    private void setupActionBar() {
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        actionBarTitle = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        actionBarTitle.setText(getResources().getString(R.string.notice_board_message_activity_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add_user);
        icon.setVisibility(View.GONE);

        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            actionBar.setCustomView(viewActionBar);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
