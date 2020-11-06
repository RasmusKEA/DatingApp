package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {
    UserRepository ur = new UserRepository();
    private static String uploadFolder = "src/main/resources/static/";


    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        model.addAttribute("userToDisplay", user);
        if(file.isEmpty()){
            return "redirect:/myProfile";
        }

        try{
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadFolder + user.getUserid() + file.getOriginalFilename());
            String fileName = user.getUserid() + file.getOriginalFilename();
            String pathToDB = uploadFolder + user.getUserid() + file.getOriginalFilename();
            Files.write(path, bytes);

            ur.updateImagePath(fileName, user.getUserid());

        } catch (IOException e) {
        e.printStackTrace();
    }
        return "redirect:/myProfile";
    }

}
