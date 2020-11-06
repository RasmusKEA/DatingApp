package com.example.demo.repositories;

import com.example.demo.models.User;
import com.example.demo.services.UserServices;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

import java.sql.*;
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

    public boolean verifyUserLogin(String email, String password) {
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
                return true;
            } else {
                System.out.println("Username eller password er ikke korrekt");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void findRandomUser(int maxID) {
        Random r = new Random();
        int result = r.nextInt((maxID + 1) - 2) + 2;
        System.out.println("result: " + result);

        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT fullname FROM users where userid like ?;");
            ps.setInt(1, result);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){

                rs.getString(1);
                System.out.println(rs.getString(1));
            }


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
        findRandomUser(maxID);
        return maxID;
    }

    public User findUserByMail(String email) {
        PreparedStatement ps = null;
        User user = null;
        try {
            ps = establishConnection().prepareStatement("SELECT email, username, fullname FROM users WHERE email like ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String dbEmail = rs.getString(1);
            String dbUsername = rs.getString(3);
            String dbFullName = rs.getString(3);
            user = new User(dbFullName, dbUsername, dbEmail);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
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

    public String getBio(String email) {
        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT bio FROM users where email like ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String bio = rs.getString(1);
            System.out.println(rs.getString(1));
            return bio;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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



}



