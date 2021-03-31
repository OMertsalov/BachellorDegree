package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.util.List;

import com.example.demo.model.Receipt;

import net.sourceforge.tess4j.TesseractException;

public interface ReceiptReaderService {

	public List <String> readLines(List<BufferedImage> lines,String language) throws TesseractException;
	public Receipt readReceiptData(List<BufferedImage> lines,String language) throws TesseractException;
}
