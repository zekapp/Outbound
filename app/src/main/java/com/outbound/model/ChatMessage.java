package com.outbound.model;

import com.parse.ParseFile;

import java.util.Date;

/**
 * Created by zeki on 21/10/2014.
 */
public class ChatMessage {

    private String text;
    private NSDate date;
    private String userID;
    private ParseFile profilePicture;
    private String userName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ParseFile getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ParseFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public NSDate getDate() {
        return date;
    }

    public void setDate(NSDate date) {
        this.date = date;
    }
}
