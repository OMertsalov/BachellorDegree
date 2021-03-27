package com.example.demo.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Results;
import com.example.demo.services.opencv.OpenCvService;
import com.example.demo.services.tesseract.ReceiptReaderService;

import net.sourceforge.tess4j.TesseractException;



@Controller
public class HomeController {
	
	
	private OpenCvService openCvService;
	private ReceiptReaderService receiptReaderService;
	
	private List<Results> results = new ArrayList<>();
	
	
	@Autowired
	public HomeController(OpenCvService openCvService, ReceiptReaderService receiptReaderService) {
		this.openCvService = openCvService;
		this.receiptReaderService = receiptReaderService;
	}
	
	@ModelAttribute
	public void addAttributes(Model model) {
	    model.addAttribute("results", results);
	}

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
        	List<BufferedImage> lines = openCvService.getReceiptLines(image.getBytes());
        	List <String> textLines = receiptReaderService.readLines(lines,"/default");
        	List <String> auchanLang = receiptReaderService.readLines(lines,"");
        	results = new ArrayList<>();
        	for(int i=0; i < lines.size();i++) {
        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        		ImageIO.write(lines.get(i), "png", baos);
	            String imageAsBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
	            results.add(new Results(imageAsBase64, textLines.get(i), auchanLang.get(i)));
        	}
        	attributes.addFlashAttribute("results", results);     	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TesseractException e) {
			e.printStackTrace();
		}

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded receipt.");

        return "redirect:/home";
    }
}
