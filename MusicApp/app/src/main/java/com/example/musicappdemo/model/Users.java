package com.example.musicappdemo.model;

public class Users {
    private int id;
    private String email;
    private String gender;
    private byte[] image;
    public Users() {
    }

    public Users(int id, String email, String gender, byte[] image) {
        this.id = id;
        this.email = email;
        this.gender = gender;
        this.image = image;
    }

    public Users(String email, String gender, byte[] image) {
        this.email = email;
        this.gender = gender;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
