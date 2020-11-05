package com.nduta.inventory;

public class UserDetails {
    private String username;
    private String section;

    public UserDetails() {
    }

    public UserDetails(String username, String section) {
        this.username = username;
        this.section = section;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
