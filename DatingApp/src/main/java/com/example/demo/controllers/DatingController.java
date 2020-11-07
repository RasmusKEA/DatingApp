package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLIntegrityConstraintViolationException;

@Controller
public class DatingController {
    UserRepository ur = new UserRepository();

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
    public String myProfile(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        if(user!=null){
            return "myProfile";
        } else{
            return "redirect:/";
        }
    }

    @GetMapping("/explore")
    public String explore(HttpServletRequest testRequest, Model model){
        HttpSession testSession = testRequest.getSession();
        User testUser = (User) testSession.getAttribute("testUser");
        model.addAttribute("testUser", testUser);
        System.out.println(testUser.getFullName());

        return "explore.html";
    }

    @GetMapping("/candidateList")
    public String candidateList(){
        return "candidateList.html";
    }


    @PostMapping("/loginPost")
    public String formPost(HttpServletRequest request, Model model){
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = ur.login(email, password);

        if(user == null){
            return "redirect:/";
        }



        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        return "redirect:/myProfile";
    }


    @PostMapping("/registerPost")
    public String registerPost(HttpServletRequest request, Model model){
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            ur.createUser(username, password, name, email);
        } catch (SQLIntegrityConstraintViolationException throwables) {
            throwables.printStackTrace();
            System.out.println("Den email eller brugernavn er allerede i brug på siden");
        }

        return "redirect:/";
    }

    @PostMapping("/infoPost")
    public String myProfileBio(HttpServletRequest request, Model model){
        String bio = request.getParameter("bio");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(user.getEmail());
        System.out.println(bio);

        UserRepository ur = new UserRepository();
        ur.saveUserBio(bio, user.getEmail());
        return "redirect:/myProfile";
    }

    @PostMapping("/PasswordPost")
    public String changePassword(HttpServletRequest request, Model model){
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String newPassword1 = request.getParameter("newPassword1");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        UserRepository ur = new UserRepository();
        ur.updatePassword(oldPassword, newPassword, newPassword1,user.getUserid());

        return "redirect:/myProfile";
    }


    @PostMapping("/EmailPost")
    public String changeEmail(HttpServletRequest request, Model model){
        String oldEmail = request.getParameter("oldEmail");
        String newEmail = request.getParameter("newEmail");
        String newEmail1 = request.getParameter("newEmail1");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        UserRepository ur = new UserRepository();
        ur.updateEmail(oldEmail, newEmail, newEmail1,user.getUserid());

        return "redirect:/myProfile";
    }

    @PostMapping("/explorePost")
    public String explorePost(HttpServletRequest testRequest, Model userModel){

        User testUser = ur.findExploreUser();
        HttpSession testSession = testRequest.getSession();
        testSession.setAttribute("testUser", testUser);

        return "redirect:/explore";
    }
}
