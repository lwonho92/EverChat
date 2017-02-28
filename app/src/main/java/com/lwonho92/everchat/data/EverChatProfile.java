package com.lwonho92.everchat.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MY on 2017-02-11.
 */

@IgnoreExtraProperties
public class EverChatProfile {
    private String userName;
    private String photoUrl;
    private String country;
    private String language;
    private String email;
    private String info;
    private HashMap<String, Boolean> stars;

    public EverChatProfile() {

    }
    public EverChatProfile(String userName, String photoUrl, String country, String language, String info, HashMap<String, Boolean> stars) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.country = country;
        this.language = language;
        this.info = info;
        this.stars = stars;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public HashMap<String, Boolean> getStars() {
        if(stars == null)
            stars = new HashMap<String, Boolean>();
        return stars;
    }

    public void setStars(HashMap<String, Boolean> stars) {
        this.stars = stars;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", userName);
        result.put("photo", photoUrl);
        result.put("country", country);
        result.put("language", language);
        result.put("email", email);
        result.put("info", info);
        result.put("stars", stars);
        return result;
    }
}
