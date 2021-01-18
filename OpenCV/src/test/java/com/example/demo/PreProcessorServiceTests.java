package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.services.PreProcessorService;

@SpringBootTest
class PreProcessorServiceTests {
	
	@Autowired
	PreProcessorService preProcessorService;
	
	@Test
	void preProcessingImages() {
		String folderPath = "/home/alexander/WorkFolder/Tesseract/Images";
		preProcessorService.processImages(folderPath);
	}

}
