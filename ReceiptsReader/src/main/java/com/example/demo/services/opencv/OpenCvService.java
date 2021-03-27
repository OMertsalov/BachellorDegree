package com.example.demo.services.opencv;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface OpenCvService {
	
	public List<BufferedImage> getReceiptLines(byte[] image) throws IOException;
	
}
