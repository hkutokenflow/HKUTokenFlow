package com.example.workshop1.SQLite;


import java.io.Serializable;

public class VendorApproval implements Serializable {
    private String username;
    private String password;
    private String name;
    private int approved;

    public VendorApproval(String n, String p, String name){
        this.username = n;
        this.password = p;
        this.name = name;
        this.approved = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) { this.approved = approved; }
}
