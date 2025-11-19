package com.communityevents.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("date")
    private String date; // Format: "YYYY-MM-DD" or "MM/DD/YYYY"
    
    @JsonProperty("time")
    private String time; // Format: "HH:MM AM/PM"
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("organizer")
    private String organizer;
    
    @JsonProperty("creatorId")
    private int creatorId;

    public Event() {
    }

    public Event(int id, String title, String description, String date, String time, 
                 String location, String category, String organizer, int creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.organizer = organizer;
        this.creatorId = creatorId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }
}

