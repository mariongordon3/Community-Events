package com.communityevents.ui;

import com.communityevents.model.Event;
import com.communityevents.model.Comment;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UserInterface {
    private static UserInterface instance;

    private UserInterface() {
    }

    public static synchronized UserInterface getInstance() {
        if (instance == null) {
            instance = new UserInterface();
        }
        return instance;
    }

    public void displayEventList(Context ctx, List<Event> events) {
        Map<String, Object> response = new HashMap<>();
        response.put("events", events);
        response.put("count", events.size());
        ctx.json(response);
    }

    public void displayEventDetails(Context ctx, Event event) {
        if (event == null) {
            displayError(ctx, "Event not found", 404);
            return;
        }
        ctx.json(event);
    }

    public void displayComments(Context ctx, List<Comment> comments) {
        Map<String, Object> response = new HashMap<>();
        response.put("comments", comments);
        response.put("count", comments.size());
        ctx.json(response);
    }

    public void displayFilters(Context ctx) {
        Map<String, Object> response = new HashMap<>();
        response.put("filters", List.of("category", "date", "location", "keyword"));
        ctx.json(response);
    }

    public void displayLogin(Context ctx) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Please provide email and password");
        ctx.json(response);
    }

    public void displayError(Context ctx, String message, int statusCode) {
        ctx.status(statusCode);
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        ctx.json(response);
    }

    public void displaySuccess(Context ctx, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        ctx.json(response);
    }

    public void displayUserData(Context ctx, Object userData) {
        ctx.json(userData);
    }
}

