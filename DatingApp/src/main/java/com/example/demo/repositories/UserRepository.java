package com.example.demo.repositories;

import com.example.demo.models.User;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

import java.sql.*;
import java.util.Random;

public class UserRepository {
    private Connection establishConnection() throws SQLException {
        //Lav en forbindelse
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dating?serverTimezone=UTC","user","password");


        return conn;
    }

    public void createUser(String username, String password, String name, String email) throws SQLIntegrityConstraintViolationException{



        PreparedStatement ps = null;
        try {
            ps = establishConnection().prepareStatement("INSERT INTO users (username, password, fullname, email) VALUES (?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, email);

            ps.executeUpdate();
            System.out.println("Din konto er nu blevet oprettet");
        } catch (SQLException e) {
            if(e instanceof SQLIntegrityConstraintViolationException){
                System.out.println("Email eller brugernavn er allerede i brug " + e.getMessage());
            }
            System.out.println("Fejl i createUser " + e.getMessage());
        }


        //TODO Sørg for der ikke kan være flere med samme email og username



    }

    public boolean verifyUserLogin(String email, String password){
        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT email, password FROM users WHERE email like ? AND password like ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String dbEmail = rs.getString(1);
            String dbPword = rs.getString(2);

            if(email.equals(dbEmail) && password.equals(dbPword)){
                System.out.println("Korrekt login, velkommen");
                return true;
            }else{
                System.out.println("Username eller password er ikke korrekt");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void findRandomUser(int maxID){
        Random r = new Random();
        int result = r.nextInt((maxID + 1) - 2) + 2;
        System.out.println("result: " + result);

        try {
            PreparedStatement ps = establishConnection().prepareStatement("SELECT fullname FROM users where userid like ?;");
            ps.setInt(1, result);
            ResultSet rs = ps.executeQuery();
            rs.next();
            rs.getString(1);
            System.out.println(rs.getString(1));



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int findMax(){
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


}
