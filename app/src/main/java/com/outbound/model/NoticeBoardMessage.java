package com.outbound.model;

import com.parse.ParseFile;

import java.util.Date;

/**
 * Created by zeki on 18/10/2014.
 */
public class NoticeBoardMessage {
    private String text;
    private ParseFile profilePicture;
    private String userName;
    private String userID;
    private String date;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
