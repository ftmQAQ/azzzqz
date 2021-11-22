package com.example.azzzqz.javabean;

public class User {
    private String portrait_img;
    private String username,password;
    private int account;
    private int result;
    private int frireq_count;
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAccount() {
        return account;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccount(int number) {
        this.account = number;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public String getPortrait_img() {
        return portrait_img;
    }

    public void setPortrait_img(String portrait_img) {
        this.portrait_img = portrait_img;
    }

    public void setFrireq_count(int frireq_count) {
        this.frireq_count = frireq_count;
    }

    public int getFrireq_count() {
        return frireq_count;
    }
}