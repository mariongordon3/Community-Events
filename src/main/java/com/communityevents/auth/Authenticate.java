package com.communityevents.auth;

import com.communityevents.database.Database;
import com.communityevents.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Authenticate {
    private static Authenticate instance;
    private Database database;
    private Map<Integer, Boolean> loggedInUsers; // userId -> isLoggedIn

    private Authenticate() {
        this.database = Database.getInstance();
        this.loggedInUsers = new ConcurrentHashMap<>();
    }

    public static synchronized Authenticate getInstance() {
        if (instance == null) {
            instance = new Authenticate();
        }
        return instance;
    }

    public User authenticate(String email, String password) {
        User user = database.getUser(email);
        
        if (user != null && user.getPassword().equals(password)) {
            user.setLoggedIn(true);
            loggedInUsers.put(user.getUserId(), true);
            return user;
        }
        
        return null;
    }

    public boolean isLoggedIn(int userId) {
        return loggedInUsers.getOrDefault(userId, false);
    }

    public boolean logout(int userId) {
        User user = database.getUserById(userId);
        if (user != null) {
            user.setLoggedIn(false);
        }
        loggedInUsers.remove(userId);
        return true;
    }

    public User getLoggedInUser(int userId) {
        if (isLoggedIn(userId)) {
            return database.getUserById(userId);
        }
        return null;
    }

    public void markUserAsLoggedIn(int userId) {
        loggedInUsers.put(userId, true);
        User user = database.getUserById(userId);
        if (user != null) {
            user.setLoggedIn(true);
        }
    }
}

