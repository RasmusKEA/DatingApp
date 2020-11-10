package com.example.demo.repositories;

import com.example.demo.models.Candidate;
import com.example.demo.models.User;
import com.example.demo.services.UserServices;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class UserRepository {
    private Connection establishConnection() throws SQLException {
        //Lav en forbindelse
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dating?serverTimezone=UTC", "user", "password");
        return conn;
    }

    public User createUser(String username, String password, String name, String email) throws SQLIntegrityConstraintViolationException {
        User user = null;


        PreparedStatement ps = null;
        try {
            ps = establishConnection().prepareStatement("INSERT INTO users (username, password, fullname, email) VALUES (?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, email);
            user = new User(name, username, password, email);
            ps.executeUpdate();

            PreparedStatement ps1 = establishConnection().prepareStatement("SELECT userid FROM users where email = ?");
            ps1.setString(1, email);
            ResultSet rs = ps1.executeQuery();
            rs.next();
            int ownerid = rs.getInt(1);
            createCandidateList(ownerid);
            System.out.println("Din konto er nu blevet oprettet");
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                System.out.println("Email eller brugernavn er allerede i brug " + e.getMessage());
            }
            System.out.println("Fejl i createUser " + e.getMessage());
        }

        return user;

        //TODO Sørg for der ikke kan være flere med samme email og username
    }

    public void createCandidateList(int ownerid){
        try {
            PreparedStatement ps = establishConnection().prepareStatement("INSERT INTO candidatelist (ownerid) VALUES (?)");
            ps.setInt(1, ownerid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int findMax() {
        int maxID = 0;
        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT COUNT(*) FROM users;");
            ResultSet rs = ps.executeQuery();
            rs.next();
            rs.getInt(1);
            maxID = rs.getInt(1);
            System.out.println(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxID;
    }

    public Candidate findExploreUser() {
        Candidate candidate = null;
        User user = null;
        int maxID = findMax();
        Random r = new Random();
        int result = r.nextInt((maxID + 1) - 2) + 2;
        System.out.println("result: " + result);

        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT userid, fullname, bio, imagepath FROM users where userid like ?;");
            ps.setInt(1, result);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.getString(1);
                candidate = new Candidate(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                System.out.println(rs.getInt(1) + rs.getString(2) + rs.getString(3) + rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidate;
    }

    public User login(String email, String password) {
        User user = null;
        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT email, password FROM users WHERE email like ? AND password like ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String dbEmail = rs.getString(1);
            String dbPword = rs.getString(2);

            if (email.equals(dbEmail) && password.equals(dbPword)) {
                System.out.println("Korrekt login, velkommen");
                user = new User(email, password);
                return fullUserObject(email);
            } else {
                System.out.println("Username eller password er ikke korrekt");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUserBio(String bio, String email) {
        PreparedStatement ps = null;

        try {
            ps = establishConnection().prepareStatement("UPDATE users SET bio = ? WHERE (email = ?)");

            ps.setString(1, bio);
            ps.setString(2, email);
            ps.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public User fullUserObject(String email) {
        User user = null;

        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT userid, username, password, fullname, email, bio, imagepath FROM users WHERE email like ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int dbUserID = rs.getInt(1);
            String dbUsername = rs.getString(2);
            String dbPassword = rs.getString(3);
            String dbFullName = rs.getString(4);
            String dbEmail = rs.getString(5);
            String dbBio = rs.getString(6);
            String dbImagePath = rs.getString(7);

            user = new User(dbUserID, dbUsername, dbPassword, dbFullName, dbEmail, dbBio, dbImagePath);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    public void updateImagePath(String imagePath, int userID){
        PreparedStatement ps = null;
        try {
            ps = establishConnection().prepareStatement("UPDATE users SET imagepath = ? WHERE (userid = ?)");

            ps.setString(1, imagePath);
            ps.setInt(2, userID);
            ps.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public boolean updatePassword(String oldPassword, String newPassword, String newPassword1, int userID){
        UserServices us = new UserServices();
        PreparedStatement ps = null;
        try {
            ps = establishConnection().prepareStatement("SELECT password FROM users where userid like ?");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String oldDBPassword = rs.getString(1);

            boolean test = us.verifyPasswordChange(oldPassword, oldDBPassword, newPassword, newPassword1);
            if (test) {
                ps = establishConnection().prepareStatement("UPDATE users SET password = ? WHERE (userid = ?)");
                ps.setString(1, newPassword);
                ps.setInt(2, userID);
                ps.executeUpdate();
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    public boolean updateEmail(String oldEmail, String newEmail, String newEmail1, int userID){
        UserServices us = new UserServices();
        PreparedStatement ps = null;
        try {
            ps = establishConnection().prepareStatement("SELECT email FROM users where userid like ?");

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String oldDBEmail = rs.getString(1);
            us.verifyEmailChange(oldEmail, newEmail, newEmail1, oldDBEmail);

            if (us.verifyEmailChange(oldEmail, oldDBEmail, newEmail, newEmail1) == true) {
                ps = establishConnection().prepareStatement("UPDATE users SET email = ? WHERE (userid = ?)");
                ps.setString(1, newEmail);
                ps.setInt(2, userID);
                ps.executeUpdate();
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void addToCandidateList(int ownerid, int candidateid){

        String candID = String.valueOf(candidateid);

        try {
            PreparedStatement ps1 = establishConnection().prepareStatement("SELECT usersInList FROM candidatelist WHERE ownerid = ?");
            ps1.setInt(1, ownerid);
            ResultSet rs = ps1.executeQuery();
            rs.next();
            String candidates = rs.getString(1);
            System.out.println(candidates);

            PreparedStatement ps = establishConnection().prepareStatement("UPDATE candidatelist SET usersInList = ? WHERE (ownerid = ?)");
            if(candidates == null || candidates.isEmpty() || candidates.length() == 0){

                ps.setString(1, candID);
                ps.setInt(2, ownerid);
                ps.executeUpdate();
            }else{
                candID = candID + ", " + candidates;
                ps.setString(1, candID);
                ps.setInt(2, ownerid);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Candidate> listOfCandidates(int ownerid){
        ArrayList<Candidate> listOfCandidates = new ArrayList<>();
        PreparedStatement ps = null;
        try {
            ps = establishConnection().prepareStatement("SELECT usersInList FROM candidatelist WHERE ownerid = ?");
            ps.setInt(1, ownerid);

            ResultSet rs = ps.executeQuery();
            rs.next();
            String candidates = rs.getString(1);

            if(candidates != null){
                String[] arr = candidates.split(", ");
                for (int i = 0; i < arr.length; i++) {
                    System.out.println(arr);
                    Candidate user = fullUserObjectByID(arr[i]);
                    listOfCandidates.add(user);
                    System.out.println(user.getFullName());

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfCandidates;
    }

    public Candidate fullUserObjectByID(String userid) {
        Candidate user = null;

        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT userid, fullname, bio, imagepath  FROM users WHERE userid like ?");
            ps.setString(1, userid);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int dbUserID = rs.getInt(1);
            String dbFullName = rs.getString(2);
            String dbBio = rs.getString(3);
            String dbImagePath = rs.getString(4);

            user = new Candidate(dbUserID, dbFullName, dbBio, dbImagePath);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean isInCandList(int ownerid, int candid){
        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT usersInList FROM candidatelist WHERE ownerid = ?");
            ps.setInt(1, ownerid);

            ResultSet rs = ps.executeQuery();
            rs.next();
            String usersInList = rs.getString(1);

            if(usersInList != null){
                String[] arr = usersInList.split(", ");

                String candID = String.valueOf(candid);
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals(candID)){
                        System.out.println("is in cand list: " + arr[i].equals(candID));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCandListFull(int ownerid){
        int maxID = findMax();
        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT usersInList FROM candidatelist WHERE ownerid = ?");
            ps.setInt(1, ownerid);

            ResultSet rs = ps.executeQuery();
            rs.next();
            String usersInList = rs.getString(1);
            System.out.println("usersinlist string: " + usersInList);

            if(usersInList != null){
                String[] arr = usersInList.split(", ");
                if(arr.length-2 == maxID || arr.length-1 == maxID){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}



