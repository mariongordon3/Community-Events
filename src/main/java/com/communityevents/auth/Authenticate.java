package com.communityevents.auth;

import com.communityevents.database.Database;
import com.communityevents.database.DatabaseConnectionManager;
import com.communityevents.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Authenticate {
    private static Authenticate instance;
    private Database database;
    private DatabaseConnectionManager connectionManager;
    private Map<Integer, Boolean> loggedInUsers; // userId -> isLoggedIn

    private Authenticate() {
        this.database = Database.getInstance();
        this.connectionManager = DatabaseConnectionManager.getInstance();
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
            updateUserLoginStatus(user.getUserId(), true);
            user.setLoggedIn(true);
            loggedInUsers.put(user.getUserId(), true);
            return user;
        }
        
        return null;
    }

    public boolean isLoggedIn(int userId) {
        // Check both in-memory cache and database
        if (loggedInUsers.containsKey(userId)) {
            return loggedInUsers.get(userId);
        }
        User user = database.getUserById(userId);
        if (user != null && user.isLoggedIn()) {
            loggedInUsers.put(userId, true);
            return true;
        }
        return false;
    }

    public boolean logout(int userId) {
        updateUserLoginStatus(userId, false);
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
        updateUserLoginStatus(userId, true);
        loggedInUsers.put(userId, true);
        User user = database.getUserById(userId);
        if (user != null) {
            user.setLoggedIn(true);
        }
    }

    private void updateUserLoginStatus(int userId, boolean isLoggedIn) {
        String sql = "UPDATE users SET is_logged_in = ? WHERE user_id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isLoggedIn);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user login status: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

