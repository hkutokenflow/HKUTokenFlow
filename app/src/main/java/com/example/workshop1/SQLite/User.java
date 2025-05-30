package com.example.workshop1.SQLite;


import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String name;
    private String type;
    private int balance;

    public User(String n,String p, String name, String type){
        this.username = n;
        this.password = p;
        this.name = name;
        this.type = type;
        this.balance = 0;
    }

    public User(String n, String p, String name, String type, int balance) {
        this.username = n;
        this.password = p;
        this.name = name;
        this.type = type;
        this.balance = balance;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
