package com.example.demo.controllers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.naming.Binding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.ReceiptForm;
import com.example.demo.services.opencv.OpenCvService;
import com.example.demo.services.receipt.ReceiptService;
import com.example.demo.services.tesseract.ReceiptReaderService;

import net.sourceforge.tess4j.TesseractException;

@Controller
@SessionAttributes("receiptForm")
public class HomeController {

	private OpenCvService openCvService;
	private ReceiptReaderService receiptReaderService;
	private ReceiptService receiptService;

	@Autowired
	public HomeController(OpenCvService openCvService, ReceiptReaderService receiptReaderService,
			ReceiptService receiptService) {
		this.openCvService = openCvService;
		this.receiptReaderService = receiptReaderService;
		this.receiptService = receiptService;
	}

	@ModelAttribute(name = "receiptForm")
	public ReceiptForm receiptForm() {
		return new ReceiptForm();
	}

	@GetMapping("/home")
	public String home() {
		return "home";
	}

	@PostMapping("/addReceiptData")
	public String addReceiptData(@ModelAttribute ReceiptForm receiptForm, BindingResult result, SessionStatus status,
			RedirectAttributes attributes) {
		receiptService.validateData(receiptForm,result);
		System.out.println(result.getErrorCount());
		if (result.hasFieldErrors()) {
			return "home";
		}
		attributes.addFlashAttribute("message", "Dane zostały dodane.");
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
			ReceiptForm receiptForm = receiptReaderService.readReceiptData(lines, "auchan");
			String imageAsBase64 = Base64.getEncoder().encodeToString(image.getBytes());
			attributes.addFlashAttribute("imageAsBase64", imageAsBase64);
			attributes.addFlashAttribute("receiptForm", receiptForm);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		attributes.addFlashAttribute("message", "You successfully uploaded receipt.");
		return "redirect:/home";
	}
}