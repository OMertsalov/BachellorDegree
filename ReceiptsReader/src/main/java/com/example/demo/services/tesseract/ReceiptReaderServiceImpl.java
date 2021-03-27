package com.example.demo.services.tesseract;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class ReceiptReaderServiceImpl implements ReceiptReaderService{

	private Tesseract tesseract;

	public ReceiptReaderServiceImpl() {
		tesseract = new Tesseract();
		tesseract.setLanguage("pol");
		tesseract.setOcrEngineMode(1);
		tesseract.setTessVariable("user_defined_dpi", "300");
	}

	@Override
	public List <String> readLines(List<BufferedImage> lines,String language) throws TesseractException {
		List <String> textLines = new ArrayList<>();
		double avgLineHeight = lines.stream().mapToDouble(bi -> bi.getTileHeight()).average().getAsDouble();
		avgLineHeight += avgLineHeight/2;
		tesseract.setDatapath("src/main/resources/tessdata" + language);
		for(BufferedImage image : lines) {
			if(image.getHeight() > avgLineHeight) {
				tesseract.setPageSegMode(4);
				System.out.println("there");
			}
			else
				tesseract.setPageSegMode(7);
			String result = tesseract.doOCR(image);
			textLines.add(result);
		}
		return textLines;
	}	
	
}
