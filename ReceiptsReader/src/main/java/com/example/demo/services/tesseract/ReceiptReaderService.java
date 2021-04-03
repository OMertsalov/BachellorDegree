package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.util.List;

import com.example.demo.model.Result;

import net.sourceforge.tess4j.TesseractException;

public interface ReceiptReaderService {

	public Result readReceiptData(List<BufferedImage> lines,String language) throws TesseractException;
}
