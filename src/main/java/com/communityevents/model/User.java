package com.communityevents.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("userId")
    private int userId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("isLoggedIn")
    private boolean isLoggedIn;
    
    // Password should NOT be serialized to JSON for security
    private String password; // For authentication purposes

    public User() {
    }

    public User(int userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLoggedIn = false;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Methods from class diagram
    public void viewEvents() {
        // This will be handled by the controller
    }

    public void searchEvents(String keyword) {
        // This will be handled by the controller
    }

    public void addComment(int eventId, String text) {
        // This will be handled by the controller
    }

    public void editComment(int commentId, String text) {
        // This will be handled by the controller
    }

    public void deleteComment(int commentId) {
        // This will be handled by the controller
    }

    public void createEvent(Object eventData) {
        // This will be handled by the controller
    }

    public void login(String email, String password) {
        // This will be handled by the controller
    }

    public void logout() {
        this.isLoggedIn = false;
    }
}

