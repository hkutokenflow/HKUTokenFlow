package com.example.workshop1.Admin.Event;

public class EventItem {
    String name;
    int tokens;

    public EventItem(String name, int tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public int getTokens() {
        return tokens;
    }
}
