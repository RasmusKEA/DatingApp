package com.example.demo.models;

public class User {
    private String fullName, username, password, email, bio, imageName;

    private int userid;

    public User(int userid, String username, String password, String fullName, String email, String bio) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userid = userid;
        this.bio = bio;

    }
    public User (int userid, String username, String password, String fullName, String email, String bio, String imageName){
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userid = userid;
        this.bio = bio;
        this.imageName = imageName;
    }

    public User(String fullName, String username, String password, String email) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String fullName, String username, String email){
        this.fullName = fullName;
        this.username = username;
        this.email = email;
    }

    public User(String email, String password){
        this.password = password;
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public String getBioBox(){
        return bio;
    }

    public void setBio(String bio) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
