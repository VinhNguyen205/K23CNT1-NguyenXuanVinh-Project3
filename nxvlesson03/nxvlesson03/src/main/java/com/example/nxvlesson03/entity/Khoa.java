package com.example.nxvlesson03.entity;

public class Khoa {
    private String makh;
    private String tenkh;

    // Constructors
    public Khoa() {
    }

    public Khoa(String makh, String tenkh) {
        this.makh = makh;
        this.tenkh = tenkh;
    }

    // Getters and Setters
    public String getMakh() {
        return makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getTenkh() {
        return tenkh;
    }

    public void setTenkh(String tenkh) {
        this.tenkh = tenkh;
    }
}