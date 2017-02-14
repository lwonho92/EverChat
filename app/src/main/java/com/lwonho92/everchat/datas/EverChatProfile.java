package com.lwonho92.everchat.datas;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MY on 2017-02-11.
 */

@IgnoreExtraProperties
public class EverChatProfile {
    private String country;
    private String language;
    private String profile;

    public EverChatProfile() {

    }
    public EverChatProfile(String country, String language, String profile) {
        this.country = country;
        this.language = language;
        this.profile = profile;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("country", country);
        result.put("language", language);
        result.put("profile", profile);
        return result;
    }
}
