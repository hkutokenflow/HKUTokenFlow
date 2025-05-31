package com.example.workshop1.Admin.Vendor;

public class VendorApprovalItem {
    private String username;
    private String password;
    private String name;
    private int approved; // 0: pending, 1: approved, 2: refused

    public VendorApprovalItem(String username, String password, String name, int approved) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.approved = approved;
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

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public String getStatusText() {
        switch (approved) {
            case 1:
                return "Approved";
            case -1:
                return "Refused";
            default:
                return "Pending";
        }
    }

    public int getStatusColor() {
        switch (approved) {
            case 1:
                return 0xC04CAF50; // Green for approved (25% transparent)
            case -1:
                return 0xC0F44336; // Red for refused (25% transparent)
            default:
                return 0xC0FFA500; // Orange for pending (25% transparent)
        }
    }
}