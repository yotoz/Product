package com.example.user.model;

import com.google.firebase.database.IgnoreExtraProperties;

public class User {
    // user types
    public final static String ADMIN_TYPE = "admin";
    public final static String USER_TYPE = "user";

    private String admin;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
