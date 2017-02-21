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
    private String profile;
    private HashMap<String, Boolean> stars = new HashMap<>();

    public EverChatProfile() {

    }
    public EverChatProfile(String userName, String photoUrl, String country, String language, String profile) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.country = country;
        this.language = language;
        this.profile = profile;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public HashMap<String, Boolean> getStars() {
        return stars;
    }

    public void setStars() {
        this.stars = new HashMap<>();
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
        result.put("profile", profile);
        return result;
    }

    public Map<String, Object> toStarsMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("stars", stars);
        return result;
    }
}
