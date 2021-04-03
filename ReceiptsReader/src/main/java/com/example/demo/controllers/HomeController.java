package com.example.demo.controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.List;


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
	
	@PostMapping("/addMarket")
    public String addNewMarket(SessionStatus status,RedirectAttributes attributes,@ModelAttribute("result")Result result) {
		attributes.addFlashAttribute("message", "Sklep został dodany.");
		
		if(!marketRepository.existsByAddress(result.getReceipt().getMarket().getAddress())) {
//			marketRepository.save(result.getReceipt().getMarket());
			//Make train
		}
		status.setComplete();
        return "redirect:/home";
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

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded receipt.");
        
        return "redirect:/home";
    }
}
