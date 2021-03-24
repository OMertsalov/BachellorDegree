package com.example.demo.services;

public interface PreProcessorService {

	public void processImages(String folderPath);
	
	public void processImageForLearning(String folderPath);
	
	public void processImageForOCR(String folderPath);
	
	
	
}
