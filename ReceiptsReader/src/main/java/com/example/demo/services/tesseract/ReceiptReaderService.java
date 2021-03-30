package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.util.List;

import net.sourceforge.tess4j.TesseractException;

public interface ReceiptReaderService {

	public List <String> readLines(List<BufferedImage> lines,String language) throws TesseractException;
	public String readReceiptData(List<BufferedImage> lines,String language) throws TesseractException;
}
