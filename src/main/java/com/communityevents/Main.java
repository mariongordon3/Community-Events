package com.communityevents;

import com.communityevents.controller.Controller;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Controller controller = Controller.getInstance();
        
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static", Location.CLASSPATH);
        }).start(7000);

        // Add CORS headers manually
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");
        });

        // Handle OPTIONS requests for CORS preflight
        app.options("/*", ctx -> {
            ctx.status(200);
        });

        // Story 1: View Events
        app.get("/api/events", controller::handleViewEvents);

        // Story 2: Search Events
        app.get("/api/events/search", controller::handleSearch);

        // Get comments for an event (must come before /api/events/{id})
        app.get("/api/events/{id}/comments", controller::handleGetComments);

        // Get event details
        app.get("/api/events/{id}", controller::handleGetEventDetails);

        // Story 4: Create Event (requires auth)
        app.post("/api/events", controller::handleEventCreation);

        // Update Event (requires auth, only creator can update)
        app.put("/api/events/{id}", controller::handleUpdateEvent);

        // Delete Event (requires auth, only creator can delete)
        app.delete("/api/events/{id}", controller::handleDeleteEvent);

        // Story 3: Comment Actions (requires auth)
        app.post("/api/events/{eventId}/comments", controller::handleCommentAction);
        app.put("/api/comments/{id}", controller::handleCommentAction);
        app.delete("/api/comments/{id}", controller::handleCommentAction);

        // Story 5: Authentication
        app.post("/api/auth/login", controller::handleLogin);
        app.post("/api/auth/register", controller::handleRegister);
        app.post("/api/auth/logout", controller::handleLogout);
        app.get("/api/auth/status", controller::handleAuthStatus);

        // Handle 404 for unmatched routes
        app.error(404, ctx -> {
            if (ctx.path().startsWith("/api")) {
                ctx.json(Map.of("error", "API endpoint not found: " + ctx.path()));
            }
        });

        System.out.println("Server running on http://localhost:7000");
    }
}

