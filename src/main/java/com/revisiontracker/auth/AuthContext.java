package com.revisiontracker.auth;

public class AuthContext {
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setCurrentUser(String userId) {
        CURRENT_USER.set(userId);
    }

    public static String currentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
