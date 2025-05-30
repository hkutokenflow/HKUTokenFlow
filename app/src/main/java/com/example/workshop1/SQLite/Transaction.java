package com.example.workshop1.SQLite;

public class Transaction {
    public String datetime;
    public int source;
    public int destination;
    public int amount;
    public int erid;
    public String ttype;


    public Transaction(String dt,int s, int d, int a, int er, String t) {
        this.datetime = dt;
        this.source = s;
        this.destination = d;
        this.amount = a;
        this.erid = er;
        this.ttype = t;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String dt) {
        this.datetime = dt;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int n) {
        this.source = n;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int n) {
        this.destination = n;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int n) {
        this.amount = n;
    }

    public int getErid() {
        return erid;
    }

    public void setErid(int e) { this.erid = e; }

    public String getTtype() {
        return ttype;
    }

    public void setTtype(String e) { this.ttype = e; }
}
