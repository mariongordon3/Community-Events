package com.communityevents.database;

import com.communityevents.model.Event;
import com.communityevents.model.Comment;
import com.communityevents.model.User;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    private static Database instance;
    private List<Event> events;
    private List<Comment> comments;
    private List<User> users;
    private int nextEventId;
    private int nextCommentId;
    private int nextUserId;

    private Database() {
        this.events = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.users = new ArrayList<>();
        this.nextEventId = 1;
        this.nextCommentId = 1;
        this.nextUserId = 1;
        initializeSampleData();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Initialize sample users
        users.add(new User(nextUserId++, "John Doe", "john@example.com", "password123"));
        users.add(new User(nextUserId++, "Jane Smith", "jane@example.com", "password123"));
        users.add(new User(nextUserId++, "Bob Johnson", "bob@example.com", "password123"));

        // Initialize sample events
        events.add(new Event(nextEventId++, "Neighborhood Cleanup", 
            "Join us for a community cleanup day!", "2025-05-12", "10:00 AM", 
            "City Park", "Community", "John Doe", 1));
        events.add(new Event(nextEventId++, "Farmers Market", 
            "Local vendors, produce, and crafts!", "2025-05-13", "8:00 AM", 
            "Main Street", "Market", "Jane Smith", 2));
        events.add(new Event(nextEventId++, "Yoga in the Park", 
            "Free yoga session for all levels", "2025-05-14", "9:00 AM", 
            "City Park", "Fitness", "Bob Johnson", 3));
        events.add(new Event(nextEventId++, "Art Workshop", 
            "Learn painting techniques from local artists", "2025-05-15", "5:00 PM", 
            "Community Center", "Art", "Jane Smith", 2));

        // Initialize sample comments
        comments.add(new Comment(nextCommentId++, 2, 1, "Can't wait for this event!", "John Doe"));
        comments.add(new Comment(nextCommentId++, 2, 3, "Will there be parking nearby?", "Bob Johnson"));
    }

    // Event methods
    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public Event getEventDetails(int eventId) {
        return events.stream()
                .filter(e -> e.getId() == eventId)
                .findFirst()
                .orElse(null);
    }

    public List<Event> searchEvents(Map<String, String> filters) {
        List<Event> result = new ArrayList<>(events);

        if (filters == null || filters.isEmpty()) {
            return result;
        }

        String keyword = filters.get("keyword");
        String category = filters.get("category");
        String date = filters.get("date");
        String location = filters.get("location");

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            result = result.stream()
                    .filter(e -> (e.getTitle() != null && e.getTitle().toLowerCase().contains(lowerKeyword)) ||
                                (e.getCategory() != null && e.getCategory().toLowerCase().contains(lowerKeyword)) ||
                                (e.getOrganizer() != null && e.getOrganizer().toLowerCase().contains(lowerKeyword)))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.trim().isEmpty()) {
            result = result.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (date != null && !date.trim().isEmpty()) {
            result = result.stream()
                    .filter(e -> e.getDate() != null && e.getDate().equals(date))
                    .collect(Collectors.toList());
        }

        if (location != null && !location.trim().isEmpty()) {
            result = result.stream()
                    .filter(e -> e.getLocation() != null && e.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public Event saveEvent(Event eventData) {
        eventData.setId(nextEventId++);
        events.add(eventData);
        return eventData;
    }

    public boolean deleteEvent(int eventId) {
        // Delete all comments associated with this event
        comments.removeIf(c -> c.getEventId() == eventId);
        
        // Delete the event
        return events.removeIf(e -> e.getId() == eventId);
    }

    // Comment methods
    public List<Comment> getCommentsForEvent(int eventId) {
        return comments.stream()
                .filter(c -> c.getEventId() == eventId)
                .collect(Collectors.toList());
    }

    public Comment saveComment(int eventId, String text, int userId, String userName) {
        Comment comment = new Comment(nextCommentId++, eventId, userId, text, userName);
        comments.add(comment);
        return comment;
    }

    public Comment editComment(int commentId, String text) {
        Comment comment = comments.stream()
                .filter(c -> c.getId() == commentId)
                .findFirst()
                .orElse(null);
        
        if (comment != null) {
            comment.setText(text);
        }
        return comment;
    }

    public boolean deleteComment(int commentId) {
        return comments.removeIf(c -> c.getId() == commentId);
    }

    // User methods
    public User getUser(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public User getUserById(int userId) {
        return users.stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User saveUser(User user) {
        // Check if email already exists
        if (getUser(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Set userId if not already set
        if (user.getUserId() == 0) {
            user.setUserId(nextUserId++);
        }
        
        users.add(user);
        return user;
    }
}

