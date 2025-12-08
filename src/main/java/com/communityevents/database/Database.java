package com.communityevents.database;

import com.communityevents.model.Event;
import com.communityevents.model.Comment;
import com.communityevents.model.User;
import java.sql.*;
import java.util.*;

public class Database {
    private static Database instance;
    private DatabaseConnectionManager connectionManager;
    private boolean initialized = false;

    private Database() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
        initializeSchema();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void initializeSchema() {
        if (initialized) {
            return;
        }

        try (Connection conn = connectionManager.getConnection()) {
            // Read schema.sql and execute it
            String schema = 
                "CREATE TABLE IF NOT EXISTS users (" +
                    "user_id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "is_logged_in BOOLEAN DEFAULT FALSE" +
                "); " +
                "CREATE TABLE IF NOT EXISTS events (" +
                    "id SERIAL PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "date VARCHAR(50) NOT NULL, " +
                    "time VARCHAR(50) NOT NULL, " +
                    "location VARCHAR(255) NOT NULL, " +
                    "category VARCHAR(100), " +
                    "organizer VARCHAR(255), " +
                    "creator_id INTEGER REFERENCES users(user_id) ON DELETE SET NULL" +
                "); " +
                "CREATE TABLE IF NOT EXISTS comments (" +
                    "id SERIAL PRIMARY KEY, " +
                    "event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE, " +
                    "user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE, " +
                    "text TEXT NOT NULL, " +
                    "user_name VARCHAR(255) NOT NULL, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); " +
                "CREATE INDEX IF NOT EXISTS idx_users_email ON users(email); " +
                "CREATE INDEX IF NOT EXISTS idx_events_creator_id ON events(creator_id); " +
                "CREATE INDEX IF NOT EXISTS idx_events_category ON events(category); " +
                "CREATE INDEX IF NOT EXISTS idx_events_date ON events(date); " +
                "CREATE INDEX IF NOT EXISTS idx_comments_event_id ON comments(event_id); " +
                "CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id);";

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(schema);
            }

            // Initialize sample data if tables are empty
            initializeSampleData(conn);
            initialized = true;
        } catch (SQLException e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void initializeSampleData(Connection conn) {
        try {
            // Check if users table is empty
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return; // Data already exists
                }
            }

            // Insert sample users
            try (PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (name, email, password, is_logged_in) VALUES (?, ?, ?, ?) RETURNING user_id")) {
                
                // User 1
                userStmt.setString(1, "John Doe");
                userStmt.setString(2, "john@example.com");
                userStmt.setString(3, "password123");
                userStmt.setBoolean(4, false);
                userStmt.execute();
                ResultSet rs1 = userStmt.getResultSet();
                rs1.next();
                int userId1 = rs1.getInt(1);

                // User 2
                userStmt.setString(1, "Jane Smith");
                userStmt.setString(2, "jane@example.com");
                userStmt.setString(3, "password123");
                userStmt.setBoolean(4, false);
                userStmt.execute();
                ResultSet rs2 = userStmt.getResultSet();
                rs2.next();
                int userId2 = rs2.getInt(1);

                // User 3
                userStmt.setString(1, "Bob Johnson");
                userStmt.setString(2, "bob@example.com");
                userStmt.setString(3, "password123");
                userStmt.setBoolean(4, false);
                userStmt.execute();
                ResultSet rs3 = userStmt.getResultSet();
                rs3.next();
                int userId3 = rs3.getInt(1);

                // Insert sample events
                try (PreparedStatement eventStmt = conn.prepareStatement(
                        "INSERT INTO events (title, description, date, time, location, category, organizer, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id")) {
                    
                    // Event 1
                    eventStmt.setString(1, "Neighborhood Cleanup");
                    eventStmt.setString(2, "Join us for a community cleanup day!");
                    eventStmt.setString(3, "2025-05-12");
                    eventStmt.setString(4, "10:00 AM");
                    eventStmt.setString(5, "City Park");
                    eventStmt.setString(6, "Community");
                    eventStmt.setString(7, "John Doe");
                    eventStmt.setInt(8, userId1);
                    eventStmt.execute();

                    // Event 2
                    eventStmt.setString(1, "Farmers Market");
                    eventStmt.setString(2, "Local vendors, produce, and crafts!");
                    eventStmt.setString(3, "2025-05-13");
                    eventStmt.setString(4, "8:00 AM");
                    eventStmt.setString(5, "Main Street");
                    eventStmt.setString(6, "Market");
                    eventStmt.setString(7, "Jane Smith");
                    eventStmt.setInt(8, userId2);
                    eventStmt.execute();
                    ResultSet evtRs2 = eventStmt.getResultSet();
                    evtRs2.next();
                    int eventId2 = evtRs2.getInt(1);

                    // Event 3
                    eventStmt.setString(1, "Yoga in the Park");
                    eventStmt.setString(2, "Free yoga session for all levels");
                    eventStmt.setString(3, "2025-05-14");
                    eventStmt.setString(4, "9:00 AM");
                    eventStmt.setString(5, "City Park");
                    eventStmt.setString(6, "Fitness");
                    eventStmt.setString(7, "Bob Johnson");
                    eventStmt.setInt(8, userId3);
                    eventStmt.execute();

                    // Event 4
                    eventStmt.setString(1, "Art Workshop");
                    eventStmt.setString(2, "Learn painting techniques from local artists");
                    eventStmt.setString(3, "2025-05-15");
                    eventStmt.setString(4, "5:00 PM");
                    eventStmt.setString(5, "Community Center");
                    eventStmt.setString(6, "Art");
                    eventStmt.setString(7, "Jane Smith");
                    eventStmt.setInt(8, userId2);
                    eventStmt.execute();

                    // Insert sample comments
                    try (PreparedStatement commentStmt = conn.prepareStatement(
                            "INSERT INTO comments (event_id, user_id, text, user_name) VALUES (?, ?, ?, ?)")) {
                        
                        commentStmt.setInt(1, eventId2);
                        commentStmt.setInt(2, userId1);
                        commentStmt.setString(3, "Can't wait for this event!");
                        commentStmt.setString(4, "John Doe");
                        commentStmt.execute();

                        commentStmt.setInt(1, eventId2);
                        commentStmt.setInt(2, userId3);
                        commentStmt.setString(3, "Will there be parking nearby?");
                        commentStmt.setString(4, "Bob Johnson");
                        commentStmt.execute();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
            // Don't throw - sample data is optional
        }
    }

    // Event methods
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, title, description, date, time, location, category, organizer, creator_id FROM events ORDER BY date, time";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting events: " + e.getMessage());
            e.printStackTrace();
        }
        
        return events;
    }

    public Event getEventDetails(int eventId) {
        String sql = "SELECT id, title, description, date, time, location, category, organizer, creator_id FROM events WHERE id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting event details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Event> searchEvents(Map<String, String> filters) {
        List<Event> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT id, title, description, date, time, location, category, organizer, creator_id FROM events WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (filters != null) {
            String keyword = filters.get("keyword");
            String category = filters.get("category");
            String date = filters.get("date");
            String location = filters.get("location");
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (LOWER(title) LIKE ? OR LOWER(category) LIKE ? OR LOWER(organizer) LIKE ?)");
                String keywordPattern = "%" + keyword.toLowerCase() + "%";
                params.add(keywordPattern);
                params.add(keywordPattern);
                params.add(keywordPattern);
            }
            
            if (category != null && !category.trim().isEmpty()) {
                sql.append(" AND LOWER(category) = ?");
                params.add(category.toLowerCase());
            }
            
            if (date != null && !date.trim().isEmpty()) {
                sql.append(" AND date = ?");
                params.add(date);
            }
            
            if (location != null && !location.trim().isEmpty()) {
                sql.append(" AND LOWER(location) LIKE ?");
                params.add("%" + location.toLowerCase() + "%");
            }
        }
        
        sql.append(" ORDER BY date, time");
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching events: " + e.getMessage());
            e.printStackTrace();
        }
        
        return events;
    }

    public Event saveEvent(Event eventData) {
        String sql = "INSERT INTO events (title, description, date, time, location, category, organizer, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, eventData.getTitle());
            stmt.setString(2, eventData.getDescription());
            stmt.setString(3, eventData.getDate());
            stmt.setString(4, eventData.getTime());
            stmt.setString(5, eventData.getLocation());
            stmt.setString(6, eventData.getCategory());
            stmt.setString(7, eventData.getOrganizer());
            stmt.setInt(8, eventData.getCreatorId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    eventData.setId(rs.getInt(1));
                    return eventData;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving event: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public Event updateEvent(int eventId, Event eventData) {
        String sql = "UPDATE events SET title = ?, description = ?, date = ?, time = ?, location = ?, category = ?, organizer = ? WHERE id = ? RETURNING id, title, description, date, time, location, category, organizer, creator_id";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, eventData.getTitle());
            stmt.setString(2, eventData.getDescription());
            stmt.setString(3, eventData.getDate());
            stmt.setString(4, eventData.getTime());
            stmt.setString(5, eventData.getLocation());
            stmt.setString(6, eventData.getCategory());
            stmt.setString(7, eventData.getOrganizer());
            stmt.setInt(8, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    // Comment methods
    public List<Comment> getCommentsForEvent(int eventId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT id, event_id, user_id, text, user_name, timestamp FROM comments WHERE event_id = ? ORDER BY timestamp DESC";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return comments;
    }

    public Comment saveComment(int eventId, String text, int userId, String userName) {
        String sql = "INSERT INTO comments (event_id, user_id, text, user_name) VALUES (?, ?, ?, ?) RETURNING id, event_id, user_id, text, user_name, timestamp";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.setString(3, text);
            stmt.setString(4, userName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving comment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public Comment editComment(int commentId, String text) {
        String sql = "UPDATE comments SET text = ? WHERE id = ? RETURNING id, event_id, user_id, text, user_name, timestamp";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, text);
            stmt.setInt(2, commentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error editing comment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean deleteComment(int commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    // User methods
    public User getUser(String email) {
        String sql = "SELECT user_id, name, email, password, is_logged_in FROM users WHERE email = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, name, email, password, is_logged_in FROM users WHERE user_id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email, password, is_logged_in FROM users";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }

    public User saveUser(User user) {
        // Check if email already exists
        if (getUser(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        String sql = "INSERT INTO users (name, email, password, is_logged_in) VALUES (?, ?, ?, ?) RETURNING user_id";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isLoggedIn());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save user", e);
        }
        
        return null;
    }

    // Helper methods to map ResultSet to model objects
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setDate(rs.getString("date"));
        event.setTime(rs.getString("time"));
        event.setLocation(rs.getString("location"));
        event.setCategory(rs.getString("category"));
        event.setOrganizer(rs.getString("organizer"));
        event.setCreatorId(rs.getInt("creator_id"));
        return event;
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setEventId(rs.getInt("event_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setText(rs.getString("text"));
        comment.setUserName(rs.getString("user_name"));
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            comment.setTimestamp(timestamp.toLocalDateTime().toString());
        }
        return comment;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setLoggedIn(rs.getBoolean("is_logged_in"));
        return user;
    }
}
