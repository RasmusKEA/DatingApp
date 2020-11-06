package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {
    private static String uploadFolder = "src/main/java/profilepictures/";


    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return "redirect:/myProfile";
        }

        try{
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadFolder + "useridgoeshere" +file.getOriginalFilename());
            String fileName = "userID" + file.getOriginalFilename();
            Files.write(path, bytes);


        } catch (IOException e) {
        e.printStackTrace();
    }
        return "redirect:/myProfile";
    }

}
