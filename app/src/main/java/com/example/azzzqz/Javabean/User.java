package com.example.azzzqz.Javabean;

public class User {
    private String portrait_img;
    private String username,password,sex,phone;
    private int account;
    private int result;
    private int age;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

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
}