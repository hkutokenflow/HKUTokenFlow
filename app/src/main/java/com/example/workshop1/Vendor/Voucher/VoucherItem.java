package com.example.workshop1.Vendor.Voucher;

public class VoucherItem {
    String name;
    String description;
    int tokens;

    public VoucherItem(String name, String d, int tokens) {
        this.name = name;
        this.description = d;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public int getTokens() {
        return tokens;
    }

    public String getDescription() {
        return description;
    }
}
