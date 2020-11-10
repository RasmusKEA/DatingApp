package com.example.demo.models;

public class Message {
    private int toID;
    private String message;
    private int fromID;
    private int userID;
    private String fullName;
    private String idAndName;



    public Message(int toID, String message, int fromID){
        this.fromID = fromID;
        this.toID = toID;
        this.message = message;
    }

    public Message(String fullName, String message){
        this.message = message;
        this.fullName = fullName;
    }

    public int getUserID() { return userID; }

    public void setUserID(int userID) { this.userID = userID; }

    public int getToID() { return toID; }

    public void setToID(int toID) { this.toID = toID; }

    public int getFromID() { return fromID; }

    public void setFromID(int fromID) { this.fromID = fromID; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

}
