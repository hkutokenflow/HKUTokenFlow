package com.example.workshop1.SQLite;

public class StudentReward {
    public int uid;
    public int rid;

    public StudentReward(int u, int r) {
        this.uid = u;
        this.rid = r;
    }

    public int getUid() {
        return uid;
    }
    
    public int getRid() {
        return rid;
    }
}
