package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.util.List;

import com.example.demo.model.ReceiptForm;

import net.sourceforge.tess4j.TesseractException;

public interface ReceiptReaderService {

	public ReceiptForm readReceiptData(List<BufferedImage> lines,String language) throws TesseractException;
}
