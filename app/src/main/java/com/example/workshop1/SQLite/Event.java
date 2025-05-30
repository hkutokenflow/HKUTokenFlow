package com.example.workshop1.SQLite;

public class Event {
    private String description;
    private int reward;

    public Event(String d, int r) {
        this.description = d;
        this.reward = r;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int r) {
        this.reward = r;
    }

}
