package com.example.demo.controllers;

import com.example.demo.models.Candidate;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.mysql.cj.PreparedQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

@Controller
public class DatingController {
    //testUser = candidate user
    //user = logged in user

    //TODO Giv roller til alle der opretter sig + de som der er i DB i forvejen. (0 = admin, 1 = user, 2 = blacklisted)
    //TODO Lav HTML og CSS for admin view + messages. (find smartest måde at sende beskeder)
    //TODO Fix exceptions f.eks. ved forkert login. - lav pop up med fejlbesked
    //TODO Få gjort candidateList pæn
    //TODO Fix crash ved fuld candList


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

        System.out.println(user.getUsergroup());

        if(user!=null){
            return "myProfile";
        } else{
            return "redirect:/";
        }
    }

    @GetMapping("/explore")
    public String explore(HttpServletRequest candRequest, Model model){
        HttpSession candSession = candRequest.getSession();
        Candidate candUser = (Candidate) candSession.getAttribute("candUser");
        if(candUser!= null){
            model.addAttribute("candUser", candUser);
        }else{
            Candidate noCandUser = new Candidate(0, "Desværre!", "Der er ikke flere kandidater", "sad.png");
            model.addAttribute("candUser", noCandUser);

        }

        return "explore.html";
    }

    @GetMapping("/candidateList")
    public String candidateList(HttpServletRequest candReq, Model model){
        HttpSession candSession = candReq.getSession();
        ArrayList<Candidate> candList = (ArrayList<Candidate>) candSession.getAttribute("candList");
        model.addAttribute("candList", candList);
        return "candidateList.html";
    }

    @GetMapping("/memberlist")
    public String memberlist(Model model, HttpServletRequest allReq, HttpServletRequest request) {
        HttpSession allSession = allReq.getSession();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        ArrayList<User> allUsers = (ArrayList<User>) allSession.getAttribute("allUsers");
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", user);


        return "/memberlist.html";
    }

    @GetMapping("/messages")
    public String messages(HttpServletRequest msgRequest, Model model) {
        HttpSession msgSession = msgRequest.getSession();
        ArrayList<Candidate> candList = (ArrayList<Candidate>) msgSession.getAttribute("candList");
        model.addAttribute("candList", candList);

        ArrayList<Message> msgList = (ArrayList<Message>) msgSession.getAttribute("arrOfMsg");

        boolean emptyList = false;

        if(msgList == null){
            emptyList = true;
        }
        model.addAttribute("emptyList", emptyList);
        model.addAttribute("arrOfMsg", msgList);

        return "/messages.html";
    }


    @PostMapping("/loginPost")
    public String formPost(HttpServletRequest request, RedirectAttributes redirAttrs){
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = ur.login(email, password);



        if(user == null){
            redirAttrs.addFlashAttribute("message", "Forkert brugernavn eller kodeord. Prøv igen!");
            return "redirect:/";
        }else if(user.getUsergroup() == 2){
            redirAttrs.addFlashAttribute("message", "Du er blevet blacklisted pga. dårlig opførsel!");
            return "redirect:/";
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        return "redirect:/myProfile";
    }


    @PostMapping("/registerPost")
    public String registerPost(HttpServletRequest request){
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

    @PostMapping("/PasswordPost")
    public String changePassword(HttpServletRequest request){
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
    public String changeEmail(HttpServletRequest request){
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
    public String explorePost(HttpServletRequest candRequest, HttpServletRequest request){

        Candidate candUser = null;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        candUser = ur.findExploreUser(user.getUserid());

        HttpSession candSession = candRequest.getSession();
        candSession.setAttribute("candUser", candUser);

        return "redirect:/explore";
    }

    @PostMapping("/notAddPost")
    public String notAddPost(HttpServletRequest candRequest, HttpServletRequest request){
        return explorePost(candRequest, request);
    }

    @PostMapping("/addToCandidate")
    public String addToCandidate(HttpServletRequest candRequest, Model model, HttpServletRequest request){
        HttpSession candSession = candRequest.getSession();
        Candidate candUser = (Candidate) candSession.getAttribute("candUser");
        model.addAttribute("candUser", candUser);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if(candUser != null){
            ur.addToCandidateList(user.getUserid(), candUser.getUserid());
            return explorePost(candRequest, request);
        }
        return explorePost(candRequest, request);
    }

    @PostMapping("/adminMemberList")
    public String adminMemberList(HttpServletRequest allReq){
        HttpSession allSession = allReq.getSession();
        ArrayList<User> allUsers = ur.getAllUsers();
        allSession.setAttribute("allUsers", allUsers);

        return "redirect:/memberlist";
    }

    @PostMapping("/showCandidateList")
    public String showCandidateList(HttpServletRequest request, HttpServletRequest candReq){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        ArrayList<Candidate> candList = ur.listOfCandidates(user.getUserid());

        HttpSession candSession = candReq.getSession();
        candSession.setAttribute("candList", candList);
        return "redirect:/candidateList";
    }

    @PostMapping("/showMsgReceivers")
    public String showMsgReceivers(HttpServletRequest request, HttpServletRequest msgRequest){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(user.getUserid());

        ArrayList<Candidate> candList = ur.listOfCandidates(user.getUserid());

        HttpSession msgSession = msgRequest.getSession();
        msgSession.setAttribute("candList", candList);


        ArrayList<Message> arrOfMsg = ur.receiveMsg(user.getUserid());
        msgSession.setAttribute("arrOfMsg", arrOfMsg);

        return "redirect:/messages";
    }


    @PostMapping("/logout")
    public String logout(HttpServletRequest request){


        HttpSession requestSession = request.getSession(false);

        if(requestSession != null){
            requestSession.invalidate();
        }

        return "redirect:/";
    }


    @PostMapping("/infoPost")
    public String myProfileBio(HttpServletRequest request){
        String bio = request.getParameter("bio");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(user.getEmail());
        System.out.println(bio);

        UserRepository ur = new UserRepository();
        ur.saveUserBio(bio, user.getEmail());
        return "redirect:/myProfile";
    }



    @PostMapping("/submitMessage")
    public String submitMessage(HttpServletRequest request, HttpServletRequest msgRequest){
        HttpSession session = request.getSession();
        HttpSession msgSession = msgRequest.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(user.getUserid());

        String toID = msgRequest.getParameter("dropdown");
        System.out.println(toID);
        int toIDasINT = Integer.parseInt(toID);

        String msg = msgRequest.getParameter("message");
        System.out.println(msg);

        UserRepository ur = new UserRepository();
        ur.sendMessage(toIDasINT, msg, user.getUserid());
        return "redirect:/messages";
    }

    @PostMapping("/blacklistUser")
    public String blacklistUser(HttpServletRequest allReq){
        String s = allReq.getParameter("userid");
        int userid = Integer.parseInt(s);
        ur.blacklistUser(userid);
        System.out.println(s);

        return adminMemberList(allReq);
    }


}
