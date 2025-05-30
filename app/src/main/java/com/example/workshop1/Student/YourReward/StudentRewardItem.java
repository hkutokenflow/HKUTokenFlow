package com.example.workshop1.Student.YourReward;

public class StudentRewardItem {
    public String voucher;
    public String description;
    public int value;
    public int uid;

    public StudentRewardItem(String voucher, String d, int v, int u) {
        this.voucher = voucher;
        this.description = d;
        this.value = v;
        this.uid = u;
    }

}
