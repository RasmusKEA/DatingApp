package com.example.demo.services;

import com.example.demo.repositories.UserRepository;

public class UserServices {
    UserRepository ur = new UserRepository();

    public boolean verifyPasswordChange(String oldPassword, String oldDbPassword, String newPassword, String newPassword1){

        if(oldPassword.equals(oldDbPassword)){
            if (newPassword.equals(newPassword1)){
                return true;
            }
        }
        return false;
    }


    public boolean verifyEmailChange(String oldEmail, String oldDBEmail, String newEmail, String newEmail1){


        if(oldEmail.equals(oldDBEmail)){
            if (newEmail.equals(newEmail1)){
                return true;
            }
        }
        return false;
    }


}
