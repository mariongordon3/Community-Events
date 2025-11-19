package com.communityevents.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("eventId")
    private int eventId;
    
    @JsonProperty("userId")
    private int userId;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("userName")
    private String userName; // For display purposes

    public Comment() {
    }

    public Comment(int id, int eventId, int userId, String text, String userName) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.text = text;
        this.userName = userName;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

