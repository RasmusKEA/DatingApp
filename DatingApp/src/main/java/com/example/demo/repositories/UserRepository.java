package com.example.demo.repositories;

import java.sql.*;

public class UserRepository {
    private Connection establishConnection() throws SQLException {
        //Lav en forbindelse
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dating","user","password");

        return conn;
    }

    private void createUser(){

    }

    public void verifyUserLogin(String email, String password){
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
            }else{
                System.out.println("Username eller password er ikke korrekt");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
