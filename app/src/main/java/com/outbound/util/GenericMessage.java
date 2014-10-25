package com.outbound.util;

import com.outbound.model.NSDate;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;
import java.util.HashMap;


/**
 * Created by zeki on 22/10/2014.
 */
public class GenericMessage{

    private String userName;
    private String userId;
    private String message;
    private Date createdDate;
    private String createdDateStr;
    private ParseFile profilePicture;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String nameSurname) {
        this.userName = nameSurname;
    }

    public String getUserID() {
        return userId;
    }

    public void setUserID(String objectId) {
        this.userId = objectId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdDate;
    }

    public void setCreatedAt(Date createdDare) {
        this.createdDate = createdDare;
    }
    public ParseFile getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ParseFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void generateFromHasMap(HashMap<String, Object> hashMap) {
        if(hashMap.get("date") instanceof Date)
            this.setCreatedAt((Date)hashMap.get("date"));
        else if(hashMap.get("date") instanceof String)
            this.setCreatedAt(new Date((String)hashMap.get("date")));
        this.setUserName((String)hashMap.get("userName"));
        this.setProfilePicture((ParseFile)hashMap.get("profilePicture"));
        this.setMessage((String)hashMap.get("text"));
        this.setUserID((String)hashMap.get("userID"));
    }
}
