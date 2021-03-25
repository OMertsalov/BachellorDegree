package com.example.demo.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.services.opencv.OpenCvService;



@Controller
public class HomeController {
	
	@Autowired
	private OpenCvService openCvService;

	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	@PostMapping("/upload")
    public String readText(@RequestParam("file") MultipartFile image, RedirectAttributes attributes) {

        // check if file is empty
        if (image.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/home";
        }

        try {
        	openCvService.getReceiptLines(image.getBytes());		
		} catch (IOException e) {
			e.printStackTrace();
		}

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded receipt.");

        return "redirect:/home";
    }
}
