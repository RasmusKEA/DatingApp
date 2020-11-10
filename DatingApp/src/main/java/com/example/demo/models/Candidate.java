package com.example.demo.models;

import java.util.Objects;

public class Candidate {
    private String fullName, imagePath, bio;
    private int userid;
    private String idAndName;

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

    public String getIdAndName() {
        return userid + " " + fullName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return userid == candidate.userid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid);
    }
}
