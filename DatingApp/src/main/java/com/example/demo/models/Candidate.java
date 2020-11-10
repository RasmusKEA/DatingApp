package com.example.demo.models;

public class Candidate {
    private String fullName, imagePath, bio;
    private int userid;

    public Candidate(int userid, String fullName, String bio, String imagePath) {
        this.userid = userid;
        this.fullName = fullName;
        this.imagePath = imagePath;
        this.bio = bio;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
