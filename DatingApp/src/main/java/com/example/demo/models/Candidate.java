package com.example.demo.models;

public class Candidate {
    private String fullName, imagePath, bio;
    private int userid;
    private String idAndName;

    public Candidate(int userid, String fullName, String imagePath, String bio) {
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

    public String getIdAndName() {
        return userid + " " + fullName;
    }
}
