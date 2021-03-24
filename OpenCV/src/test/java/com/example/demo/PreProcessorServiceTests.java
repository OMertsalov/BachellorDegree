package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.services.PreProcessorService;

import nu.pattern.OpenCV;

@SpringBootTest
class PreProcessorServiceTests {
	
	@Autowired
	PreProcessorService preProcessorService;
	
	@Test
	void preProcessingImages() {
		OpenCV.loadLocally();
		String folderPath = "/home/alexander/WorkFolder/Tesseract/Images";
		//preProcessorService.processImageForLearning(folderPath);
		folderPath += "/PreProcessedd";
		preProcessorService.processImageForOCR(folderPath);
	}

}
