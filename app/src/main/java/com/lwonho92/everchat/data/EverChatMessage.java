/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lwonho92.everchat.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class EverChatMessage {

    private String id;
    private String name;
    private String photoUrl;
    private String message;
    private String language;

    public EverChatMessage() {
    }

    public EverChatMessage(String name, String photoUrl, String message, String language) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.message = message;
        this.language = language;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    public String getLanguage() { return language; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("photoUrl", photoUrl);
        result.put("message", message);
        result.put("language", language);

        return result;
    }
}
