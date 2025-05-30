package com.example.workshop1.Student.RedeemReward;

// Reward.java
public class RewardItem {
    public String title;
    public String description;
    public int tokens;
    public int uid;

    public RewardItem(String title, String description, int tokens, int u) {
        this.title = title;
        this.description = description;
        this.tokens = tokens;
        this.uid = u;
    }
}
