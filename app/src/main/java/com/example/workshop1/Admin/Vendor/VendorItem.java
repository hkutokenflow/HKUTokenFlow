package com.example.workshop1.Admin.Vendor;

public class VendorItem {
    String username;
    String password;
    String name;

    public VendorItem(String u, String p, String n) {
        this.username = u;
        this.password = p;
        this.name = n;
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
}
