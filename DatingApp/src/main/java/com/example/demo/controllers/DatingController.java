package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;

@Controller
public class DatingController {
    User userToDisplay;

    @GetMapping("/")
    public String index(){
        return "index.html";
    }

    @GetMapping("/login")
    public String login(){
        return "login.html";
    }

    @GetMapping("/register")
    public String register(){
        return "register.html";
    }

    @GetMapping("/myProfile")
    public String myProfile(){
        return "myProfile.html";
    }

    @GetMapping("/explore")
    public String explore(){
        return "explore.html";
    }

    @GetMapping("/candidateList")
    public String candidateList(){
        return "candidateList.html";
    }


    @PostMapping("/loginPost")
    public String formPost(WebRequest wr, Model userModel){
        //Får informationen fra webrequesten
        String email = wr.getParameter("email");
        String password = wr.getParameter("password");

        UserRepository ur = new UserRepository();
        boolean loginSecure = ur.verifyUserLogin(email, password);
        ur.findMax();

        userToDisplay = ur.findUserByMail(email);
        userModel.addAttribute("userToDisplay", userToDisplay);

        if(loginSecure){

            return "myProfile";
        }else{
            return "redirect:/login";
        }


    }
    @PostMapping("/registerPost")
    public String registerPost(WebRequest wr){
        //Får informationen fra webrequesten
        String name = wr.getParameter("name");
        String email = wr.getParameter("email");
        String username = wr.getParameter("username");
        String password = wr.getParameter("password");

        UserRepository ur = new UserRepository();
        try {
            ur.createUser(username, password, name, email);
        } catch (SQLIntegrityConstraintViolationException throwables) {
            throwables.printStackTrace();
            System.out.println("Den email eller brugernavn er allerede i brug på siden");
        }

        return "redirect:/";
    }



}
