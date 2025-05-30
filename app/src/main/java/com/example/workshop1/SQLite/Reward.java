package com.example.workshop1.SQLite;

public class Reward {
    private String name;
    private String description;
    private int value;
    private int uid;

    public Reward(String n,String d, int v, int uid){
        this.name = n;
        this.description = d;
        this.value = v;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int v) {
        this.value = v;
    }

    public int getUid() {
        return uid;
    }
}
