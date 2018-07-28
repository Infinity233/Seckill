package com.Infinity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class MyController {


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void loadMultipartFile(HttpServletRequest request,MultipartFile cover) {

        String basePath = request.getServletContext().getRealPath("/sbb/");
        String imagePath = basePath + cover.getOriginalFilename();
        File desc = new File(imagePath);
        try {
            cover.transferTo(desc);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

}
