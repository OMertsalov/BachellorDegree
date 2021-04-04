package com.example.demo.controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Result;
import com.example.demo.repository.MarketRepository;
import com.example.demo.services.opencv.OpenCvService;
import com.example.demo.services.tesseract.ReceiptReaderService;

import net.sourceforge.tess4j.TesseractException;



@Controller
@SessionAttributes("result")
public class HomeController {
	
	
	private OpenCvService openCvService;
	private ReceiptReaderService receiptReaderService;
	private MarketRepository marketRepository;
	
	@Autowired
	public HomeController(OpenCvService openCvService, ReceiptReaderService receiptReaderService,MarketRepository marketRepository) {
		this.openCvService = openCvService;
		this.receiptReaderService = receiptReaderService;
		this.marketRepository = marketRepository;
	}
	
	@ModelAttribute(name = "result")
	  public Result result() {
	    return new Result();
	  }

	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	@PostMapping("/addReceiptData")
    public String addReceiptData(SessionStatus status,RedirectAttributes attributes,@ModelAttribute("result")Result result) {
		 Set<String> keys = result.getReceiptItems().keySet();
	        // printing the elements of LinkedHashMap
	     for (String key : keys) {
	    	 System.out.println("______________________________________________________");
	    	 System.out.println("OCR -> "+result.getReceiptItems().get(key).getLineTextByOCR());
	    	 System.out.println("USER -> "+result.getReceiptItems().get(key).itemLineDataToString());
	    	 System.out.println("Differences : "+ test(result.getReceiptItems().get(key).getLineTextByOCR(), result.getReceiptItems().get(key).itemLineDataToString() ));
	     }
		
		attributes.addFlashAttribute("message", "Dane zostały dodane.");
		status.setComplete();
        return "redirect:/home";
	}
	
	private int test(String seq1,String seq2) {
		StringBuilder longestSeq = new StringBuilder(seq1);
		String shortestSeq = seq2;
		if(seq1.length() < seq2.length()) {
			longestSeq = new StringBuilder(seq2);
			shortestSeq = seq1;
		}
		int lengthDiff = longestSeq.length() - shortestSeq.length(); 
		int s = longestSeq.length();int f= shortestSeq.length();
		int counter = lengthDiff;
		for(int i=0; i< shortestSeq.length(); i++) {
			int iter = i - lengthDiff; if(iter < 0) iter=0;
			boolean equal = false;
			for(int j = iter ;j <= i+lengthDiff;j++) {
				char a = shortestSeq.charAt(i);
				char b = longestSeq.charAt(j);
				if(a==b) {
					equal=true;
					longestSeq.setCharAt(j, '@');
					break;
				}
				
			}
			if(!equal) counter++; 
		}
		return counter;
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
        	Result result = receiptReaderService.readReceiptData(lines, "auchan");
            String imageAsBase64 = Base64.getEncoder().encodeToString(image.getBytes());
        	attributes.addFlashAttribute("imageAsBase64", imageAsBase64); 
        	attributes.addFlashAttribute("result", result);     	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
        attributes.addFlashAttribute("message", "You successfully uploaded receipt.");     
        return "redirect:/home";
    }
}