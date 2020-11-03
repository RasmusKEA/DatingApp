package com.example.demo.repositories;

import com.example.demo.models.User;

import java.sql.*;

public class UserRepository {
    private Connection establishConnection() throws SQLException {
        //Lav en forbindelse
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dating?useLegacyDatetimeCode=false","user","password");

        return conn;
    }

    public void createUser(String name, String email, String username, String password){
        try {
            PreparedStatement ps = establishConnection().prepareStatement("INSERT INTO users (username, password, fullname, email) VALUES (?, ?, ?, ?)");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, email);

            System.out.println(name);

            ps.executeQuery();

            ResultSet rs = ps.executeQuery();
            while(rs.next()){

            }

        } catch (SQLException e) {
        e.getMessage();

        }
    }
}
