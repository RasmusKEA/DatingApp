package com.example.demo.repositories;

import com.example.demo.models.User;

import java.sql.*;

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

    public void findRandomUser(){

    }

    private int findMax(){
        return 0;
    }


}
