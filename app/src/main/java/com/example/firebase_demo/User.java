package com.example.firebase_demo;

public class User {
    public String email;
    public String name;
    public String contact;
    public String marital;
    public User(){

    }
    public User(String email, String name, String contact, String marital) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.marital = marital;
    }
}
