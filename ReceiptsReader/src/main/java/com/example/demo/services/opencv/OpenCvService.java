package com.example.demo.services.opencv;

import org.opencv.core.Mat;

public interface OpenCvService {
	
	public Mat getReceiptLines(byte[] image);
	
}
