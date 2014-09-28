package com.outbound.view;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.ui.util.adapters.MessageListViewAdapter;
import com.outbound.util.MessageDummyClass;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by zeki on 25/09/2014.
 */
public class MessageActivity extends FragmentActivity{
    private ListView mListView;
    private MessageListViewAdapter mAdapter ;

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
                EditText messageEditText = (EditText)findViewById(R.id.ma_edit_message_text);
                MessageDummyClass cls = new MessageDummyClass();
                cls.isOwnerMessage = true;
                cls.message = messageEditText.getText().toString();
                mAdapter.addItemRight(cls);
                mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
                messageEditText.setText("");
            }
        });
    }

    private void generateTestData(){
        for (int i = 0; i < 50; i++) {
            MessageDummyClass ms = new MessageDummyClass();
            ms.isOwnerMessage = new Random().nextBoolean();
            if(ms.isOwnerMessage)
                mAdapter.addItemRight(ms);
            else
               mAdapter.addItemleft(ms);
        }
    }
    private void setUpListView() {

        mListView = (ListView)findViewById(R.id.message_list);
        mAdapter = new MessageListViewAdapter(this,testMessages);
        mListView.setAdapter(mAdapter);
        generateTestData();
    }

    private void setupActionBar() {
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.message_activity_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add_user);

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
