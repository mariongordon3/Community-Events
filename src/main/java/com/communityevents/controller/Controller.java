package com.communityevents.controller;

import com.communityevents.auth.Authenticate;
import com.communityevents.database.Database;
import com.communityevents.model.*;
import com.communityevents.ui.UserInterface;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;

public class Controller {
    private static Controller instance;
    private Database database;
    private Authenticate authenticate;
    private UserInterface userInterface;
    private ObjectMapper objectMapper;

    private Controller() {
        this.database = Database.getInstance();
        this.authenticate = Authenticate.getInstance();
        this.userInterface = UserInterface.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    // Story 1: View Community Events
    public void handleViewEvents(Context ctx) {
        List<Event> events = database.getEvents();
        userInterface.displayEventList(ctx, events);
    }

    // Story 2: Search Events
    public void handleSearch(Context ctx) {
        Map<String, String> filters = new HashMap<>();
        
        String keyword = ctx.queryParam("keyword");
        String category = ctx.queryParam("category");
        String date = ctx.queryParam("date");
        String location = ctx.queryParam("location");

        if (keyword != null) filters.put("keyword", keyword);
        if (category != null) filters.put("category", category);
        if (date != null) filters.put("date", date);
        if (location != null) filters.put("location", location);

        List<Event> events = database.searchEvents(filters);
        userInterface.displayEventList(ctx, events);
    }

    // Story 3: Manage Comments
    public void handleCommentAction(Context ctx) {
        HandlerType method = ctx.method();
        Integer userId = getUserIdFromSession(ctx);

        if (userId == null || !authenticate.isLoggedIn(userId)) {
            userInterface.displayError(ctx, "Authentication required. Please log in.", 401);
            return;
        }

        if (method == HandlerType.POST) {
            // Add comment
            try {
                int eventId = Integer.parseInt(ctx.pathParam("eventId"));
                Map<String, String> body = objectMapper.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
                String text = body.get("text");

                if (text == null || text.trim().isEmpty()) {
                    userInterface.displayError(ctx, "Comment text is required", 400);
                    return;
                }

                User user = database.getUserById(userId);
                database.saveComment(eventId, text, userId, user.getName());
                userInterface.displaySuccess(ctx, "Comment added successfully");
            } catch (Exception e) {
                userInterface.displayError(ctx, "Failed to add comment: " + e.getMessage(), 400);
            }
        } else if (method == HandlerType.PUT) {
            // Edit comment
            try {
                int commentId = Integer.parseInt(ctx.pathParam("id"));
                Map<String, String> body = objectMapper.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
                String text = body.get("text");

                if (text == null || text.trim().isEmpty()) {
                    userInterface.displayError(ctx, "Comment text is required", 400);
                    return;
                }

                // Verify comment belongs to user - need to search all events
                Comment comment = null;
                for (Event event : database.getEvents()) {
                    List<Comment> eventComments = database.getCommentsForEvent(event.getId());
                    comment = eventComments.stream()
                            .filter(c -> c.getId() == commentId)
                            .findFirst()
                            .orElse(null);
                    if (comment != null) break;
                }

                if (comment == null) {
                    userInterface.displayError(ctx, "Comment not found", 404);
                    return;
                }

                if (comment.getUserId() != userId) {
                    userInterface.displayError(ctx, "You can only edit your own comments", 403);
                    return;
                }

                database.editComment(commentId, text);
                userInterface.displaySuccess(ctx, "Comment updated successfully");
            } catch (Exception e) {
                userInterface.displayError(ctx, "Failed to edit comment: " + e.getMessage(), 400);
            }
        } else if (method == HandlerType.DELETE) {
            // Delete comment
            try {
                int commentId = Integer.parseInt(ctx.pathParam("id"));

                // Verify comment belongs to user - need to search all events
                Comment comment = null;
                for (Event event : database.getEvents()) {
                    List<Comment> eventComments = database.getCommentsForEvent(event.getId());
                    comment = eventComments.stream()
                            .filter(c -> c.getId() == commentId)
                            .findFirst()
                            .orElse(null);
                    if (comment != null) break;
                }

                if (comment == null) {
                    userInterface.displayError(ctx, "Comment not found", 404);
                    return;
                }

                if (comment.getUserId() != userId) {
                    userInterface.displayError(ctx, "You can only delete your own comments", 403);
                    return;
                }

                boolean deleted = database.deleteComment(commentId);
                if (deleted) {
                    userInterface.displaySuccess(ctx, "Comment deleted successfully");
                } else {
                    userInterface.displayError(ctx, "Failed to delete comment", 400);
                }
            } catch (Exception e) {
                userInterface.displayError(ctx, "Failed to delete comment: " + e.getMessage(), 400);
            }
        }
    }

    // Story 4: Create Event
    public void handleEventCreation(Context ctx) {
        Integer userId = getUserIdFromSession(ctx);

        if (userId == null || !authenticate.isLoggedIn(userId)) {
            userInterface.displayError(ctx, "Authentication required. Please log in.", 401);
            return;
        }

        try {
            Event eventData = objectMapper.readValue(ctx.body(), Event.class);

            // Validate required fields
            if (eventData.getTitle() == null || eventData.getTitle().trim().isEmpty()) {
                userInterface.displayError(ctx, "Event title is required", 400);
                return;
            }
            if (eventData.getDate() == null || eventData.getDate().trim().isEmpty()) {
                userInterface.displayError(ctx, "Event date is required", 400);
                return;
            }
            if (eventData.getTime() == null || eventData.getTime().trim().isEmpty()) {
                userInterface.displayError(ctx, "Event time is required", 400);
                return;
            }
            if (eventData.getLocation() == null || eventData.getLocation().trim().isEmpty()) {
                userInterface.displayError(ctx, "Event location is required", 400);
                return;
            }
            if (eventData.getDescription() == null || eventData.getDescription().trim().isEmpty()) {
                userInterface.displayError(ctx, "Event description is required", 400);
                return;
            }

            User user = database.getUserById(userId);
            eventData.setCreatorId(userId);
            if (eventData.getOrganizer() == null || eventData.getOrganizer().trim().isEmpty()) {
                eventData.setOrganizer(user.getName());
            }

            Event createdEvent = database.saveEvent(eventData);
            ctx.status(201);
            userInterface.displayEventDetails(ctx, createdEvent);
        } catch (Exception e) {
            userInterface.displayError(ctx, "Failed to create event: " + e.getMessage(), 400);
        }
    }

    // Story 5: Login
    public void handleLogin(Context ctx) {
        try {
            Map<String, String> credentials = objectMapper.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            String email = credentials.get("email");
            String password = credentials.get("password");

            if (email == null || password == null) {
                userInterface.displayError(ctx, "Email and password are required", 400);
                return;
            }

            User user = authenticate.authenticate(email, password);
            if (user != null) {
                // Store userId in session
                ctx.sessionAttribute("userId", user.getUserId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("user", user);
                response.put("message", "Login successful");
                ctx.json(response);
            } else {
                userInterface.displayError(ctx, "Invalid credentials", 401);
            }
        } catch (Exception e) {
            userInterface.displayError(ctx, "Login failed: " + e.getMessage(), 400);
        }
    }

    // Story 5: Logout
    public void handleLogout(Context ctx) {
        Integer userId = getUserIdFromSession(ctx);
        
        if (userId != null) {
            authenticate.logout(userId);
        }
        
        ctx.req().getSession().invalidate();
        userInterface.displaySuccess(ctx, "Logged out successfully");
    }

    // User Registration
    public void handleRegister(Context ctx) {
        try {
            Map<String, String> registrationData = objectMapper.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            String name = registrationData.get("name");
            String email = registrationData.get("email");
            String password = registrationData.get("password");

            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                userInterface.displayError(ctx, "Name is required", 400);
                return;
            }
            if (email == null || email.trim().isEmpty()) {
                userInterface.displayError(ctx, "Email is required", 400);
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                userInterface.displayError(ctx, "Password is required", 400);
                return;
            }

            // Check if email already exists
            if (database.getUser(email) != null) {
                userInterface.displayError(ctx, "Email already exists. Please use a different email.", 400);
                return;
            }

            // Create new user
            User newUser = new User(0, name.trim(), email.trim(), password);
            User savedUser = database.saveUser(newUser);

            // Automatically log the user in
            authenticate.markUserAsLoggedIn(savedUser.getUserId());
            ctx.sessionAttribute("userId", savedUser.getUserId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", savedUser);
            response.put("message", "Registration successful");
            ctx.status(201);
            ctx.json(response);
        } catch (IllegalArgumentException e) {
            // Email already exists error from database
            userInterface.displayError(ctx, e.getMessage(), 400);
        } catch (Exception e) {
            userInterface.displayError(ctx, "Registration failed: " + e.getMessage(), 400);
        }
    }

    // Helper method to get userId from session
    private Integer getUserIdFromSession(Context ctx) {
        Object userIdObj = ctx.sessionAttribute("userId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        } else if (userIdObj instanceof Number) {
            return ((Number) userIdObj).intValue();
        }
        return null;
    }

    // Get event details
    public void handleGetEventDetails(Context ctx) {
        try {
            int eventId = Integer.parseInt(ctx.pathParam("id"));
            Event event = database.getEventDetails(eventId);
            if (event == null) {
                userInterface.displayError(ctx, "Event not found", 404);
            } else {
                userInterface.displayEventDetails(ctx, event);
            }
        } catch (NumberFormatException e) {
            userInterface.displayError(ctx, "Invalid event ID", 400);
        } catch (Exception e) {
            userInterface.displayError(ctx, "Failed to get event details: " + e.getMessage(), 500);
        }
    }

    // Get comments for an event
    public void handleGetComments(Context ctx) {
        try {
            int eventId = Integer.parseInt(ctx.pathParam("id"));
            List<Comment> comments = database.getCommentsForEvent(eventId);
            userInterface.displayComments(ctx, comments);
        } catch (NumberFormatException e) {
            userInterface.displayError(ctx, "Invalid event ID", 400);
        }
    }

    // Check authentication status
    public void handleAuthStatus(Context ctx) {
        Integer userId = getUserIdFromSession(ctx);
        
        Map<String, Object> response = new HashMap<>();
        if (userId != null && authenticate.isLoggedIn(userId)) {
            User user = database.getUserById(userId);
            response.put("isLoggedIn", true);
            response.put("user", user);
        } else {
            response.put("isLoggedIn", false);
        }
        ctx.json(response);
    }
}

